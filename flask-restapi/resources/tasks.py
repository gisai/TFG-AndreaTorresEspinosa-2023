from flask import request, jsonify, Blueprint
from database import tasks
from algorithm import recommendation_system

tasks_bp = Blueprint('routes-tasks', __name__)


@tasks_bp.route('/post_new_user', methods=['POST']) #http://192.168.1.36:5000/post_new_user
def add_user():
    username = request.json['username']
    user_password = request.json['user_password']
    allergy_type = request.json['allergy_type']

    data = (username, user_password, allergy_type)
    user_id = tasks.insert_user(data)

    if user_id:
        user = tasks.select_user_by_id(user_id)
        return jsonify({'user' : user})
    return jsonify({'message' : 'Internal Error'})


@tasks_bp.route('/post_new_restaurant', methods=['POST']) #http://192.168.1.36:5000/post_new_user
def add_restaurant():
    restaurant_name = request.json['restaurant_name']
    restaurant_address = request.json['restaurant_address']
    pag_web = request.json['pag_web']

    data = (restaurant_name, restaurant_address, pag_web)
    restaurant_id = tasks.insert_restaurant(data)

    if restaurant_id:
        return jsonify({'restaurant_id' : restaurant_id})
    return jsonify({'message' : 'Internal Error'})


@tasks_bp.route('/post_new_rating', methods=['POST']) #http://192.168.1.36:5000/post_new_rating
def add_rating():
    data = tasks.select_id_current_user()

    id_user = data['id_user']
    id_restaurant = request.json['id_restaurant']
    rating = request.json['rating']

    data = (id_user, id_restaurant, rating)
    rating_id = tasks.insert_rating(data)

    if rating_id:
        return jsonify({'rating': data})
    return jsonify({'message': 'Internal Error'})
    


@tasks_bp.route('/get_users', methods=['GET'])
def get_users():
    data = tasks.select_all_users()

    if data:
        return jsonify({'users' : data})
    elif data == False:
        return jsonify({'message' : 'Internal Error'})
    else:
        return jsonify({'users': {}})

@tasks_bp.route('/get_recommendations_for_current_user', methods=['GET'])
def get_recommendations_for_current_user():
    data = recommendation_system.create_recomendation()

    if data:
        return jsonify({'recommendations': data})
    elif data == False:
        return jsonify({'message': 'Internal Error'})
    else:
        return jsonify({'recommendation': {}})


@tasks_bp.route('/get_restaurants_from_current_user', methods=['GET'])
def get_restaurants_from_current_user():
    data = tasks.return_restaurants_from_current_user()

    if data:
        return jsonify({'restaurants_current_user' : data})
    elif data == False:
        return jsonify({'message' : 'Internal Error'})
    else:
        return jsonify({'restaurants_current_user': {}})


@tasks_bp.route('/get_allergies_from_current_user', methods=['GET'])
def get_allergies_from_current_user():
    data = tasks.select_allergies_from_current_user()

    if data:
        return jsonify({'allergies' : data})
    elif data == False:
        return jsonify({'message' : 'Internal Error'})
    else:
        return jsonify({'allergies': {}})


@tasks_bp.route('/get_usernames', methods=['GET'])
def get_username():
    data = tasks.select_all_usernames()

    if data:
        return jsonify({'usernames' : data})
    elif data == False:
        return jsonify({'message' : 'Internal Error'})
    else:
        return jsonify({'usernames': {}})


@tasks_bp.route('/get_usernames_and_passwords', methods=['GET'])
def get_username_and_passw():
    data = tasks.select_all_usernames_and_passwords()

    if data:
        return jsonify({'usernames_and_passw' : data})
    elif data == False:
        return jsonify({'message' : 'Internal Error'})
    else:
        return jsonify({'usernames_and_passw': {}})

@tasks_bp.route('/get_current_username_with_password', methods=['GET'])
def get_current_username_with_password():
    data = tasks.select_current_username_with_password()

    if data:
        return jsonify({'current_username_with_password' : data})
    elif data == False:
        return jsonify({'message' : 'Internal Error'})
    else:
        return jsonify({'current_username_with_password': {}})


@tasks_bp.route('/get_id_current_user', methods=['GET'])
def get_id_current_user():
    data = tasks.select_id_current_user()

    if data: #{'restaurant_id' : restaurant_id}
        return jsonify({'id_current_user' : data})
    elif data == False:
        return jsonify({'message' : 'Internal Error'})
    else:
        return jsonify({'id_current_user': {}})


@tasks_bp.route('/update_user', methods=['PUT'])
def update_user():
    username = request.json['username']
    user_password = request.json['user_password']
    is_current_user = request.json['is_current_user']
    allergy_type = request.json['allergy_type']
    id_arg = request.args.get('id_user')

    if tasks.update_user(id_arg, (username,user_password,is_current_user,allergy_type,)):
        user = tasks.select_user_by_id(id_arg)
        return jsonify(user)
    return jsonify({'message' : 'Internal Error'})


@tasks_bp.route('/update_allergies', methods=['PUT'])
def update_allergies():
    allergy_type = request.json['allergy_type']

    if tasks.update_allergies((allergy_type,)):
        return jsonify({'message' : 'Allergies Updated'})
    return jsonify({'message' : 'Internal Error'})


@tasks_bp.route('/update_is_current_user', methods=['PUT'])
def update_is_current_user():
    username = request.args.get('username')
    user_password = request.args.get('user_password')
    is_current_user = request.args.get('is_current_user')

    if tasks.update_is_current_user(username,user_password,is_current_user):
        return jsonify({'message' : 'Is Current User Updated'})
    return jsonify({'message' : 'Internal Error'})