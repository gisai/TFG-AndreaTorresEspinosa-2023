import sqlite3
from sqlite3 import Error

from .connection import create_connection


def read_file(path):
    with open(path, "r") as sql_file:
        return sql_file.read()

def create_tables():
    conn = create_connection()
    try:
        cur = conn.cursor()
        cur.execute(""" CREATE TABLE IF NOT EXISTS users(
                        id_user INTEGER PRIMARY KEY AUTOINCREMENT,
                        username TEXT NOT NULL,
                        user_password TEXT NOT NULL,
                        allergy_type TEXT NOT NULL,
                        is_current_user INTEGER NOT NULL DEFAULT 0
                        )
        """)
        cur.execute(""" CREATE TABLE IF NOT EXISTS restaurants(
                        id_restaurant INTEGER PRIMARY KEY AUTOINCREMENT,
                        restaurant_name TEXT NOT NULL,
                        restaurant_address TEXT NOT NULL,
                        pag_web TEXT NOT NULL
                        )
                        
        """)
        cur.execute(""" CREATE TABLE IF NOT EXISTS ratings(
                        id_rating INTEGER PRIMARY KEY AUTOINCREMENT,
                        id_user INTEGER NOT NULL, 
                        id_restaurant INTEGER NOT NULL, 
                        rating INTEGER NOT NULL
                        )
                        
        """)
        conn.commit()
        return True
    except Error as e:
        print(f"Error at create_tables() : {str(e)}")
        return False
    finally:
        if conn:
            cur.close()
            conn.close()