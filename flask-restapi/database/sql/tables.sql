CREATE TABLE IF NOT EXISTS users(
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    username TEXT NOT NULL,
    user_password TEXT NOT NULL,
    allergy_type TEXT NOT NULL,
    is_current_user INTEGER NOT NULL DEFAULT 0
)
    
CREATE TABLE IF NOT EXISTS restaurants(
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    restaurant_name TEXT NOT NULL,
    restaurant_address TEXT NOT NULL,
    pag_web TEXT NOT NULL
)

CREATE TABLE IF NOT EXISTS ratings(
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    id_user INTEGER NOT NULL, 
    id_restaurant INTEGER NOT NULL, 
    rating INTEGER NOT NULL
)