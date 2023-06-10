import pandas as pd
import json
import csv

from database import tasks


def create_recomendation():
    labels_rat = ['id_rating', 'id_user', 'id_restaurant', 'rating']

    dict_id_current_user = tasks.select_id_current_user()
    picked_userid = dict_id_current_user['id_user']

    allergies = tasks.select_allergies_from_current_user()
    allergies_l = allergies[0]['allergy_type'].split("-")
    allergies_list = []
    for all in allergies_l:
        allergies_list.append(all.strip())
    for allergie in allergies_list:
        if allergie!= "":
            id_list = tasks.select_id_user_with_enter_allergies(allergie)
    ratings_rows = []
    for id in id_list:
        ratings_from_task = tasks.select_all_ratings_with_similar_current_allergies(id['id_user'])
        for rating in ratings_from_task:
            ratings_rows.append(rating)

    try:
        with open('csv_dct_ratings.csv', 'w') as f:
            writer = csv.DictWriter(f, fieldnames=labels_rat)
            writer.writeheader()
            for elem in ratings_rows:
                writer.writerow(elem)
    except IOError:
        print("I/O error")

    ratings = pd.read_csv('csv_dct_ratings.csv')

    restaurants_json = tasks.select_all_restaurants()
    labels_rest = ['id_restaurant', 'restaurant_name', 'restaurant_address', 'pag_web']

    try:
        with open('csv_dct_restaurants.csv', 'w') as f:
            writer = csv.DictWriter(f, fieldnames=labels_rest)
            writer.writeheader()
            for elem in restaurants_json:
                writer.writerow(elem)
    except IOError:
        print("I/O error")

    restaurants = pd.read_csv('csv_dct_restaurants.csv', encoding='latin-1')

    df = pd.merge(ratings, restaurants, on='id_restaurant', how='inner')

    restaurant_info_list = []

    matrix = df.pivot_table(index='id_user', columns='restaurant_name', values='rating')

    matrix_norm = matrix.subtract(matrix.mean(axis=1), axis = 'rows')

    user_similarity = matrix_norm.T.corr()

    if picked_userid not in user_similarity:
        create_recommendation_general(allergies_list, restaurant_info_list)
    else:
        user_similarity.drop(index=picked_userid, inplace=True)

        n = 10

        user_similarity_threshold = 0.3

        similar_users = user_similarity[user_similarity[picked_userid]>user_similarity_threshold][picked_userid].sort_values(ascending=False)[:n]

        picked_userid_eaten = matrix_norm[matrix_norm.index == picked_userid].dropna(axis=1, how='all')

        similar_user_restaurants = matrix_norm[matrix_norm.index.isin(similar_users.index)].dropna(axis=1, how='all')

        similar_user_restaurants.drop(picked_userid_eaten.columns,axis=1, inplace=True, errors='ignore')

        item_score = {}
        for i in similar_user_restaurants.columns:
            restaurant_rating = similar_user_restaurants[i]
            total = 0
            count = 0
            for u in similar_users.index:
                if pd.isna(restaurant_rating[u]) == False:
                    score = similar_users[u] * restaurant_rating[u]
                    total += score
                    count += 1
            item_score[i] = total / count

        item_score = pd.DataFrame(item_score.items(), columns=['restaurant', 'restaurant_score'])

        ranked_item_score = item_score.sort_values(by='restaurant_score', ascending=False)

        ranked_item_score_json = ranked_item_score.to_json(orient = 'index')

        dict_ranked_item_score_json = json.loads(ranked_item_score_json)

        for key, values in dict_ranked_item_score_json.items():
            restaurant = values['restaurant']
            restaurant_score = values['restaurant_score']
            restaurant_info = tasks.select_restaurant_info_by_name(restaurant)
            restaurant_info['restaurant_score'] = f"Score (0-2): {restaurant_score}"
            restaurant_info_list.append(restaurant_info)

        if len(restaurant_info_list) == 0:
             create_recommendation_general(allergies_list, restaurant_info_list)

    return restaurant_info_list


def create_recommendation_general(allergies_list, restaurant_info_list):
    restaurant_info_list.append({
        'restaurant_name': 'Lo sentimos, pero de momento no poseemos suficiente información en nuestra BBDD como para realizar una recomendación apta a sus necesidades, mientras tanto, le proporcionamos recomendaciones generales que se adaptan a sus alergias/intolerancias.',
        'restaurant_address': '',
        'pag_web': '', 'restaurant_score': ''})
    for allerg in allergies_list:
        if allerg.strip() == '':
            break
        if allerg.strip() == 'Lactosa':
            recomendaciones_generales_lactosa = [
                {'restaurant_name': 'Rodilla', 'restaurant_address': 'Travessera Jimena, 97, 6º C',
                 'pag_web': 'www.rodilla.com',
                 'restaurant_score': 'Score (0-2): Recomendación general válida para alérgicos/intolerantes a la lactosa'},
                {'restaurant_name': 'Veggie Chef', 'restaurant_address': 'Ruela Alex, 98, 3º C',
                 'pag_web': 'www.veggiechief.com',
                 'restaurant_score': 'Score (0-2): Recomendación general válida para alérgicos/intolerantes a la lactosa'},
                {'restaurant_name': 'Freshly foodie', 'restaurant_address': 'Passeig Henríquez, 244, Ático 1º',
                 'pag_web': 'www.freshlyfoodie.com',
                 'restaurant_score': 'Score (0-2): Recomendación general válida para alérgicos/intolerantes a la lactosa'},
                {'restaurant_name': 'La Carne Trémula', 'restaurant_address': 'Camino Calvo, 081, 7º 7º',
                 'pag_web': 'www.carnetremula.com',
                 'restaurant_score': 'Score (0-2): Recomendación general válida para alérgicos/intolerantes a la lactosa'},
                {'restaurant_name': 'Sabores de la tierra', 'restaurant_address': 'Rúa Garza, 900, 3º D',
                 'pag_web': 'www.saboresdelatierra.com',
                 'restaurant_score': 'Score (0-2): Recomendación general válida para alérgicos/intolerantes a la lactosa'}
            ]
            for dict in recomendaciones_generales_lactosa:
                restaurant_info_list.append(dict)
        if allerg.strip() == 'Sacarosa':
            recomendaciones_generales_sacarosa = [
                {'restaurant_name': 'Come rico y bien', 'restaurant_address': 'Ronda Crespo, 531, 95º D',
                 'pag_web': 'www.comericoybien.com',
                 'restaurant_score': 'Score (0-2): Recomendación general válida para alérgicos/intolerantes a la sacarosa'},
                {'restaurant_name': 'Ginos', 'restaurant_address': 'Plaza Moreno, 851, Bajo 0º',
                 'pag_web': 'www.ginos.com',
                 'restaurant_score': 'Score (0-2): Recomendación general válida para alérgicos/intolerantes a la sacarosa'},
                {'restaurant_name': 'Paella en casa', 'restaurant_address': 'Avenida Martos, 44, 50º C',
                 'pag_web': 'www.paellaencasa.com',
                 'restaurant_score': 'Score (0-2): Recomendación general válida para alérgicos/intolerantes a la sacarosa'},
                {'restaurant_name': 'SuperTaco', 'restaurant_address': 'Avinguda Ander, 405, 3º F',
                 'pag_web': 'www.supertaco.com',
                 'restaurant_score': 'Score (0-2): Recomendación general válida para alérgicos/intolerantes a la sacarosa'},
                {'restaurant_name': 'Cenador de Amós', 'restaurant_address': 'Avenida Sofía, 0, 4º C',
                 'pag_web': 'www.cenadordeamos.com',
                 'restaurant_score': 'Score (0-2): Recomendación general válida para alérgicos/intolerantes a la sacarosa'}
            ]
            for dict in recomendaciones_generales_sacarosa:
                restaurant_info_list.append(dict)
        if allerg.strip() == 'Gluten':
            recomendaciones_generales_gluten = [
                {'restaurant_name': "Vegan's Garden", 'restaurant_address': 'Plaça Gabriela, 372, 3º E',
                 'pag_web': 'www.vegansgarden.com',
                 'restaurant_score': 'Score (0-2): Recomendación general válida para alérgicos/intolerantes al gluten'},
                {'restaurant_name': 'Cuidate de un bocado', 'restaurant_address': 'Ronda Vanegas, 78, 96º B',
                 'pag_web': 'www.cuidatedeunbocado.com',
                 'restaurant_score': 'Score (0-2): Recomendación general válida para alérgicos/intolerantes al gluten'},
                {'restaurant_name': 'VIPS', 'restaurant_address': 'Plaça Olivia, 210, Entre suelo 8º',
                 'pag_web': 'www.vips.com',
                 'restaurant_score': 'Score (0-2): Recomendación general válida para alérgicos/intolerantes al gluten'},
                {'restaurant_name': 'Fiesta Gazpacho', 'restaurant_address': 'Bulevar Entrepeñas, 24, 15º',
                 'pag_web': 'www.fiestagazpacho.com',
                 'restaurant_score': 'Score (0-2): Recomendación general válida para alérgicos/intolerantes al gluten'},
                {'restaurant_name': 'Gusto ibérico', 'restaurant_address': 'Rúa Villalpando, 42, 4º C',
                 'pag_web': 'www.gustoiberico.com',
                 'restaurant_score': 'Score (0-2): Recomendación general válida para alérgicos/intolerantes al gluten'}
            ]
            for dict in recomendaciones_generales_gluten:
                restaurant_info_list.append(dict)
        if allerg.strip() == 'Fructosa':
            recomendaciones_generales_fructosa = [
                {'restaurant_name': 'La Tapilla Sixtina', 'restaurant_address': 'Avinguda Velásquez, 91, 7º D',
                 'pag_web': 'www.latapillasixtina.com',
                 'restaurant_score': 'Score (0-2): Recomendación general válida para alérgicos/intolerantes a la fructosa'},
                {'restaurant_name': 'FaceFood', 'restaurant_address': 'Travessera Dario, 20, 6º 0º',
                 'pag_web': 'www.facefood.com',
                 'restaurant_score': 'Score (0-2): Recomendación general válida para alérgicos/intolerantes a la fructosa'},
                {'restaurant_name': 'Fogones mexicanos', 'restaurant_address': 'Avenida Cazares, 412, 1º B',
                 'pag_web': 'www.fogonesmexicanos.com',
                 'restaurant_score': 'Score (0-2): Recomendación general válida para alérgicos/intolerantes a la fructosa'},
                {'restaurant_name': 'El rincón enchilado', 'restaurant_address': 'Travessera Fierro, 4, 5º 2º',
                 'pag_web': 'www.elrinconenchilado.com',
                 'restaurant_score': 'Score (0-2): Recomendación general válida para alérgicos/intolerantes a la fructosa'},
                {'restaurant_name': 'Casa Gachupín', 'restaurant_address': 'Passeig Hernando, 575, 78º E',
                 'pag_web': 'www.casagachupin.com',
                 'restaurant_score': 'Score (0-2): Recomendación general válida para alérgicos/intolerantes a la fructosa'}
            ]
            for dict in recomendaciones_generales_fructosa:
                restaurant_info_list.append(dict)








