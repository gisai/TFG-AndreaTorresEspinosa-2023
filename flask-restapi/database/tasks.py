import sqlite3
from sqlite3 import Error

from .connection import create_connection

def insert_user(data):
    conn = create_connection()

    sql = """ INSERT INTO users (username, user_password, allergy_type)
                VALUES(?,?,?)
    """
    
    try:
        cur = conn.cursor()
        cur.execute(sql, data)
        conn.commit()
        return cur.lastrowid
    except Error as e:
        print(f"Error at insert_user() : {str(e)}")
        return False
    
    finally:
        if conn:
            cur.close()
            conn.close()


def insert_restaurant(data):
    conn = create_connection()

    sql = """ INSERT INTO restaurants (restaurant_name, restaurant_address, pag_web)
                VALUES(?,?,?)
    """
    
    try:
        cur = conn.cursor()
        cur.execute(sql, data)
        conn.commit()
        return cur.lastrowid
    except Error as e:
        print(f"Error at insert_restaurant() : {str(e)}")
        return False
    
    finally:
        if conn:
            cur.close()
            conn.close()


def insert_rating(data):
    conn = create_connection()

    sql = """ INSERT INTO ratings (id_user, id_restaurant, rating)
                VALUES(?,?,?)
    """
    
    try:
        cur = conn.cursor()
        cur.execute(sql, data)
        conn.commit()
        return cur.lastrowid
    except Error as e:
        print(f"Error at insert_rating() : {str(e)}")
        return False
    
    finally:
        if conn:
            cur.close()
            conn.close()


def select_user_by_id(_id):
    conn = create_connection()

    sql = f"SELECT * FROM users WHERE id_user = {_id}"

    try:
        conn.row_factory = sqlite3.Row
        cur = conn.cursor()
        cur.execute(sql)
        user = dict(cur.fetchone())
        return user
    except Error as e:
        print(f"Error at select_user_by_id : {str(e)}")
        return False
    finally:
        if conn:
            cur.close()
            conn.close()


def return_restaurants_from_current_user():
    conn = create_connection()

    sql = f"SELECT * " \
          f"FROM restaurants INNER JOIN ratings ON restaurants.id_restaurant = ratings.id_restaurant " \
          f"INNER JOIN users ON users.id_user = ratings.id_user " \
          f"WHERE users.is_current_user = 1"

    try:
        conn.row_factory = sqlite3.Row
        cur = conn.cursor()
        cur.execute(sql)
        restaurants_rows = cur.fetchall()
        restaurants = [dict(row) for row in restaurants_rows]
        return restaurants
    except Error as e:
        print(f"Error at return_restaurants_from_current_user : {str(e)}")
        return False
    finally:
        if conn:
            cur.close()
            conn.close()


def select_restaurant_info_by_name(name):
    conn = create_connection()
    sql = f"SELECT * FROM restaurants WHERE restaurant_name = '{name}'"

    try:
        conn.row_factory = sqlite3.Row
        cur = conn.cursor()
        cur.execute(sql)
        restaurant = dict(cur.fetchone())
        return restaurant
    except Error as e:
        print(f"Error at select_restaurant_info_by_name : {str(e)}")
        return False
    finally:
        if conn:
            cur.close()
            conn.close()


def select_all_users():
    conn = create_connection()

    sql = "SELECT * FROM users"

    try:
        conn.row_factory = sqlite3.Row
        cur = conn.cursor()
        cur.execute(sql)
        users_rows = cur.fetchall()
        users = [ dict(row) for row in users_rows ]
        return users
    except Error as e:
        print(f"Error at select_all_users() : {str(e)}")
        return False
    finally:
        if conn:
            cur.close()
            conn.close()


def select_all_restaurants():
    conn = create_connection()

    sql = "SELECT * FROM restaurants"

    try:
        conn.row_factory = sqlite3.Row
        cur = conn.cursor()
        cur.execute(sql)
        restaurant_rows = cur.fetchall()
        restaurants = [ dict(row) for row in restaurant_rows ]
        return restaurants
    except Error as e:
        print(f"Error at select_all_restaurants() : {str(e)}")
        return False
    finally:
        if conn:
            cur.close()
            conn.close()


def select_all_ratings_with_similar_current_allergies(id):
    conn = create_connection()

    sql = f"SELECT * FROM ratings WHERE id_user IN({id})"

    try:
        conn.row_factory = sqlite3.Row
        cur = conn.cursor()
        cur.execute(sql)
        ratings_rows = cur.fetchall()
        ratings = [ dict(row) for row in ratings_rows ]
        return ratings
    except Error as e:
        print(f"Error at select_all_ratings_with_similar_current_allergies() : {str(e)}")
        return False
    finally:
        if conn:
            cur.close()
            conn.close()


def select_all_usernames():
    conn = create_connection()

    sql = f"SELECT username FROM users"

    try:
        conn.row_factory = sqlite3.Row
        cur = conn.cursor()
        cur.execute(sql)
        users_rows = cur.fetchall()
        usernames = [ dict(row) for row in users_rows ]
        return usernames
    except Error as e:
        print(f"Error at select_all_usernames : {str(e)}")
        return False
    finally:
        if conn:
            cur.close()
            conn.close()


def select_allergies_from_current_user():
    conn = create_connection()

    sql = f"SELECT allergy_type FROM users WHERE is_current_user = 1"

    try:
        conn.row_factory = sqlite3.Row
        cur = conn.cursor()
        cur.execute(sql)
        allergies_rows = cur.fetchall()
        allergies = [ dict(row) for row in allergies_rows ]
        return allergies
    except Error as e:
        print(f"Error at select_allergies_from_current_user : {str(e)}")
        return False
    finally:
        if conn:
            cur.close()
            conn.close()


def select_id_user_with_enter_allergies(all_type):
    conn = create_connection()

    sql = f"SELECT id_user FROM users WHERE allergy_type LIKE '%{all_type}%'"

    try:
        conn.row_factory = sqlite3.Row
        cur = conn.cursor()
        cur.execute(sql)
        allergies_rows = cur.fetchall()
        allergies = [ dict(row) for row in allergies_rows ]
        return allergies
    except Error as e:
        print(f"Error at select_id_user_with_enter_allergies : {str(e)}")
        return False
    finally:
        if conn:
            cur.close()
            conn.close()


def select_all_usernames_and_passwords():
    conn = create_connection()

    sql = f"SELECT username,user_password FROM users"

    try:
        conn.row_factory = sqlite3.Row
        cur = conn.cursor()
        cur.execute(sql)
        users_rows = cur.fetchall()
        usernames = [ dict(row) for row in users_rows ]
        return usernames
    except Error as e:
        print(f"Error at select_all_usernames : {str(e)}")
        return False
    finally:
        if conn:
            cur.close()
            conn.close()


def select_current_username_with_password():
    conn = create_connection()

    sql = f"SELECT username,user_password FROM users WHERE is_current_user = 1"

    try:
        conn.row_factory = sqlite3.Row
        cur = conn.cursor()
        cur.execute(sql)
        users_rows = cur.fetchall()
        usernames = [ dict(row) for row in users_rows ]
        return usernames
    except Error as e:
        print(f"Error at select_all_usernames : {str(e)}")
        return False
    finally:
        if conn:
            cur.close()
            conn.close()


def select_id_current_user():
    conn = create_connection()

    sql = f"SELECT id_user FROM users WHERE is_current_user = 1"

    try:
        conn.row_factory = sqlite3.Row
        cur = conn.cursor()
        cur.execute(sql)
        user = dict(cur.fetchone())
        return user
        
    except Error as e:
        print(f"Error at select_id_current_user : {str(e)}")
        return False
    finally:
        if conn:
            cur.close()
            conn.close()


def update_user(_id, data):
    conn = create_connection()

    sql = f""" UPDATE users SET username = ?,
                 user_password = ?,
                 is_current_user = ?,
                 allergy_type = ?
                WHERE id_user = {_id}
    """
    
    try:
        cur = conn.cursor()
        cur.execute(sql, data)
        conn.commit()
        return True
    except Error as e:
        print(f"Error at update_user() : {str(e)}")
        return False
    
    finally:
        if conn:
            cur.close()
            conn.close()


def update_allergies(data):
    conn = create_connection()

    sql = f""" UPDATE users SET allergy_type = ?
                WHERE is_current_user = 1
    """
    
    try:
        cur = conn.cursor()
        cur.execute(sql, data)
        conn.commit()
        return True
    except Error as e:
        print(f"Error at update_user() : {str(e)}")
        return False
    
    finally:
        if conn:
            cur.close()
            conn.close()


def update_is_current_user(username,user_password,is_current_user):
    conn = create_connection()

    sql = f""" UPDATE users SET is_current_user = {is_current_user}
                WHERE username = {username} AND user_password = {user_password}
    """

    try:
        cur = conn.cursor()
        cur.execute(sql)
        conn.commit()
        return True
    except Error as e:
        print(f"Error at update_is_current_user() {str(e)}")
    
    finally:
        if conn:
            cur.close()
            conn.close()

