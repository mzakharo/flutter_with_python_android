from flask import Flask
import numpy as np

app = Flask(__name__)

@app.route('/increment/<value>')
def increment(value):
    return str(np.add(int(value), 1))

app.run()
