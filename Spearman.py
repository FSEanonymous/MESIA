import scipy.stats
import json

# for RQ4 investigate the correlation of results by considering different code features
if __name__=="__main__":
    MESIA=[]
    MESIA_invocation=[]
    MESIA_variable=[]
    MESIA_code=[]

    with open("MESIA.json") as f:
        data = json.load(f)
    MESIA = data

    with open("MESIA_invocation.json") as f:
        data = json.load(f)
    MESIA_invocation = data

    with open("MESIA_variable.json") as f:
        data = json.load(f)
    MESIA_variable = data

    with open("MESIA_code.json") as f:
        data = json.load(f)
    MESIA_code = data

    print(scipy.stats.spearmanr(MESIA, MESIA_invocation))
    print(scipy.stats.spearmanr(MESIA, MESIA_variable))
    print(scipy.stats.spearmanr(MESIA, MESIA_code))
    print(scipy.stats.spearmanr(MESIA_invocation, MESIA_variable))
    print(scipy.stats.spearmanr(MESIA_invocation, MESIA_code))
    print(scipy.stats.spearmanr(MESIA_variable, MESIA_code))