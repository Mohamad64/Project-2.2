import spacy
from nltk import CFG
from nltk.parse.generate import generate

import numpy as np

grammars = []

# samples per language
N_SAMPLES = 30

# categories for our classification task
categories = ['location','schedule','travel','sports']
grammar_files = ['location.cfg','schedule.cfg','travel.cfg','sports.cfg']

# open configuration file
for description in grammar_files:
    with open(description,'r') as file:

        # context-free grammar
        cfg = CFG.fromstring(file.read())
    
        grammars.append(cfg)

def sentences(grammars):
    for cat, grammar in enumerate(grammars):

        for sentence in generate(grammar, n=N_SAMPLES):
            # stratify n samples from each class
            yield ' '.join(sentence), cat

# dataset from CFG languages
X_cfg = np.empty(N_SAMPLES*len(categories), dtype=object)
y_cfg = np.empty(N_SAMPLES*len(categories), dtype=np.uint8)
for i, (question, cat) in enumerate(sentences(grammars)):
    X_cfg[i] = question
    y_cfg[i] = cat

def data_refining(sentence):
    nlp = spacy.load('en_core_web_sm')
    all_stopwords = nlp.Defaults.stop_words
    # question_words = ['how','what','do','does','is','are','why','where','who','which',
    #                     'what time','how often']
    sent = nlp(sentence)

    tokens_refined = []
    list_of_pos = ['ADJ','ADP','AUX','CONJ','DET','DET','INTJ','NUM','PART','PRON','PUNCT',
                    'SYM','SPACE','SCONJ']

    tokens = [token for token in sent if not token in all_stopwords]

    for word in tokens:
        if word.pos_ not in list_of_pos:
            tokens_refined.append(word)

    return tokens_refined

nlp = spacy.load('en_core_web_sm')
X_data = []
for s in X_cfg:
    X_data.append(data_refining(s))

X_trai = []
for lis in X_data:
    # ''.join([token.text_with_ws for token in doc])
    X_trai.append(' '.join(token.text for token in lis))
print(X_trai)

from sklearn.model_selection import train_test_split

X_train, X_test, y_train, y_test = train_test_split(X_trai, y_cfg, test_size=0.20, random_state=42)

from sklearn import svm
from sklearn.model_selection import GridSearchCV
from sklearn2pmml import make_pmml_pipeline, sklearn2pmml
from sklearn.pipeline import Pipeline
from sklearn.metrics import confusion_matrix,classification_report
from sklearn.svm import LinearSVC
from sklearn.feature_extraction.text import TfidfVectorizer
from sklearn2pmml.feature_extraction.text import Splitter

from sklearn.linear_model import LogisticRegression
from sklearn.naive_bayes import MultinomialNB

vectorizer = TfidfVectorizer(analyzer="word", preprocessor=None, stop_words="english", ngram_range=(1,2), binary=False, use_idf=True, norm=None)
lr_model = LogisticRegression(solver='lbfgs')
nb_model = MultinomialNB()

model = svm.SVC()
param_grid = {'C': [0.1, 1, 5, 10, 20, 30, 40, 50, 60, 70, 100, 200],
                  'gamma': [1, 0.1, 0.01, 0.001],
                  'kernel': ['rbf', 'poly', 'sigmoid']}

grid = GridSearchCV(model, param_grid, refit=True)
cfg_clf = Pipeline([('tfidf',vectorizer), ('clf', grid)])

cfg_clf.fit(X_train, y_train)

y_pred = cfg_clf.predict(X_test)

print(classification_report(y_test, y_pred))

pipeline = make_pmml_pipeline(
    cfg_clf,
    active_fields = ['x'],
    target_fields = ['y'] 
)

sklearn2pmml(pipeline, 'nonlinear_svc.pmml', with_repr = True)
