import sqlite3
from sqlite3 import Error


def create_connection():
    conn = None
    try:
        conn = sqlite3.connect("C:\\Users\\andrea.torres\\Desktop\\TFG_Andrea_Torres_Espinosa\\TFG-AndreaTorresEspinosa-2023\\flask-restapi\\database\\alergiapp.db", timeout=10)
    except Error as e:
        print("Error connecting to database: " + str(e))
    return conn