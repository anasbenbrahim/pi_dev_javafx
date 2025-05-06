from builtins import len, str, bool, float, Exception
from typing import List, Dict, Any, Optional
import numpy as np
import cv2
import os
import base64
from datetime import datetime
from flask import Flask, request, jsonify
import face_recognition
from flasgger import Swagger, swag_from

app = Flask(__name__)

# Swagger configuration
app.config['SWAGGER'] = {
    'title': 'Facial Recognition API',
    'uiversion': 3,
    'version': '1.0',
    'description': 'API for facial recognition operations',
    'termsOfService': '',
    'specs_route': '/apidocs/'
}
swagger = Swagger(app)

# Configuration des dossiers
KNOWN_FACES_DIR = 'known_faces'
os.makedirs(KNOWN_FACES_DIR, exist_ok=True)

# Typage des variables globales
known_face_encodings: List[np.ndarray] = []
known_face_names: List[str] = []

@app.route('/', methods=['GET'])
def index() -> Dict[str, Any]:
    """Page d'accueil de l'API
    ---
    tags:
      - General
    responses:
      200:
        description: API information
        schema:
          id: api_info
          properties:
            name:
              type: string
              description: API name
            version:
              type: string
              description: API version
            endpoints:
              type: object
              description: Available endpoints
    """
    return jsonify({
        'name': 'Facial Recognition API',
        'version': '1.0',
        'endpoints': {
            '/': 'API information (GET)',
            '/register': 'Register a new face (POST)',
            '/recognize': 'Recognize a face (POST)',
            '/compare': 'Compare two faces (POST)'
        }
    })

def load_known_faces() -> None:
    """Charge les visages connus depuis le dossier"""
    global known_face_encodings, known_face_names
    known_face_encodings = []
    known_face_names = []

    for filename in os.listdir(KNOWN_FACES_DIR):
        if filename.endswith(('.jpg', '.png')):
            image_path = os.path.join(KNOWN_FACES_DIR, filename)
            image = face_recognition.load_image_file(image_path)
            encodings = face_recognition.face_encodings(image)

            if len(encodings) > 0:
                encoding = encodings[0]
                known_face_encodings.append(encoding)
                known_face_names.append(os.path.splitext(filename)[0])

# Chargement initial
load_known_faces()

@app.route('/register', methods=['GET'])
def register_face_info() -> Dict[str, Any]:
    """Information sur l'enregistrement d'un visage
    ---
    tags:
      - Face Registration
    responses:
      200:
        description: Registration endpoint information
        schema:
          id: register_info
          properties:
            endpoint:
              type: string
            method:
              type: string
            description:
              type: string
            required_parameters:
              type: object
            example:
              type: object
    """
    return jsonify({
        'endpoint': '/register',
        'method': 'POST',
        'description': 'Register a new face',
        'required_parameters': {
            'user_id': 'String - ID of the user',
            'image': 'String - Base64 encoded image with face'
        },
        'example': {
            'user_id': '12345',
            'image': 'data:image/jpeg;base64,/9j/4AAQSkZJRgABAQEAYABgAAD/2wBDAAIBAQIB...'
        }
    })

@app.route('/register', methods=['POST'])
def register_face() -> Dict[str, Any]:
    """Enregistre un nouveau visage
    ---
    tags:
      - Face Registration
    consumes:
      - application/json
    parameters:
      - in: body
        name: body
        description: Face registration data
        required: true
        schema:
          id: face_registration
          required:
            - user_id
            - image
          properties:
            user_id:
              type: string
              description: ID of the user
            image:
              type: string
              description: Base64 encoded image with face
    responses:
      200:
        description: Registration result
        schema:
          id: registration_result
          properties:
            success:
              type: boolean
            filename:
              type: string
            message:
              type: string
      400:
        description: Error in registration
        schema:
          id: error_response
          properties:
            success:
              type: boolean
            error:
              type: string
    """
    try:
        data: Dict[str, Any] = request.get_json()
        user_id: str = data['user_id']
        image_base64: str = data['image'].split(',')[1]
        image_bytes: bytes = base64.b64decode(image_base64)

        # Conversion en image numpy
        image_array: np.ndarray = np.frombuffer(image_bytes, dtype=np.uint8)
        image: np.ndarray = cv2.imdecode(image_array, cv2.IMREAD_COLOR)
        rgb_image: np.ndarray = cv2.cvtColor(image, cv2.COLOR_BGR2RGB)

        # Détection des visages
        face_locations: List[Any] = face_recognition.face_locations(rgb_image)

        if len(face_locations) == 0:
            return jsonify({'success': False, 'error': 'No face detected'})

        # Sauvegarde de l'image
        filename: str = f"user_{user_id}_{datetime.now().strftime('%Y%m%d%H%M%S')}.jpg"
        cv2.imwrite(os.path.join(KNOWN_FACES_DIR, filename), image)

        # Rechargement des visages
        load_known_faces()

        return jsonify({
            'success': True,
            'filename': filename,
            'message': 'Face registered successfully'
        })

    except Exception as e:
        return jsonify({
            'success': False,
            'error': str(e)
        })

@app.route('/recognize', methods=['GET'])
def recognize_face_info() -> Dict[str, Any]:
    """Information sur la reconnaissance d'un visage
    ---
    tags:
      - Face Recognition
    responses:
      200:
        description: Recognition endpoint information
        schema:
          id: recognize_info
          properties:
            endpoint:
              type: string
            method:
              type: string
            description:
              type: string
            required_parameters:
              type: object
            example:
              type: object
    """
    return jsonify({
        'endpoint': '/recognize',
        'method': 'POST',
        'description': 'Recognize a face against registered faces',
        'required_parameters': {
            'image': 'String - Base64 encoded image with face'
        },
        'example': {
            'image': 'data:image/jpeg;base64,/9j/4AAQSkZJRgABAQEAYABgAAD/2wBDAAIBAQIB...'
        }
    })

@app.route('/recognize', methods=['POST'])
def recognize_face() -> Dict[str, Any]:
    """Reconnaît un visage
    ---
    tags:
      - Face Recognition
    consumes:
      - application/json
    parameters:
      - in: body
        name: body
        description: Face recognition data
        required: true
        schema:
          id: face_recognition
          required:
            - image
          properties:
            image:
              type: string
              description: Base64 encoded image with face
    responses:
      200:
        description: Recognition result
        schema:
          id: recognition_result
          properties:
            success:
              type: boolean
            user_id:
              type: string
            confidence:
              type: number
              format: float
      400:
        description: Error in recognition
        schema:
          id: error_response
          properties:
            success:
              type: boolean
            error:
              type: string
      404:
        description: No match found
        schema:
          id: no_match_response
          properties:
            success:
              type: boolean
            error:
              type: string
    """
    try:
        data: Dict[str, Any] = request.get_json()
        image_base64: str = data['image'].split(',')[1]
        image_bytes: bytes = base64.b64decode(image_base64)

        # Conversion en image numpy
        image_array: np.ndarray = np.frombuffer(image_bytes, dtype=np.uint8)
        image: np.ndarray = cv2.imdecode(image_array, cv2.IMREAD_COLOR)
        rgb_image: np.ndarray = cv2.cvtColor(image, cv2.COLOR_BGR2RGB)

        # Détection des visages
        face_locations: List[Any] = face_recognition.face_locations(rgb_image)
        face_encodings: List[np.ndarray] = face_recognition.face_encodings(rgb_image, face_locations)

        if len(face_encodings) == 0:
            return jsonify({'success': False, 'error': 'No face detected'})

        # Comparaison avec les visages connus
        for face_encoding in face_encodings:
            matches: List[bool] = face_recognition.compare_faces(
                known_face_encodings,
                face_encoding,
                tolerance=0.5
            )

            if True in matches:
                first_match_index: int = matches.index(True)
                name: str = known_face_names[first_match_index]

                if name.startswith('user_'):
                    user_id: str = name.split('_')[1]
                    return jsonify({
                        'success': True,
                        'user_id': user_id,
                        'confidence': float(1 - face_recognition.face_distance(
                            [known_face_encodings[first_match_index]],
                            face_encoding
                        )[0])
                    })

        return jsonify({
            'success': False,
            'error': 'No match found'
        })

    except Exception as e:
        return jsonify({
            'success': False,
            'error': str(e)
        })

@app.route('/compare', methods=['GET'])
def compare_faces_info() -> Dict[str, Any]:
    """Information sur la comparaison de visages
    ---
    tags:
      - Face Comparison
    responses:
      200:
        description: Comparison endpoint information
        schema:
          id: compare_info
          properties:
            endpoint:
              type: string
            method:
              type: string
            description:
              type: string
            required_parameters:
              type: object
            example:
              type: object
    """
    return jsonify({
        'endpoint': '/compare',
        'method': 'POST',
        'description': 'Compare two faces to determine if they are the same person',
        'required_parameters': {
            'image1': 'String - Base64 encoded image with first face',
            'image2': 'String - Base64 encoded image with second face'
        },
        'example': {
            'image1': 'data:image/jpeg;base64,/9j/4AAQSkZJRgABAQEAYABgAAD/2wBDAAIBAQIB...',
            'image2': 'data:image/jpeg;base64,/9j/4AAQSkZJRgABAQEAYABgAAD/2wBDAAIBAQIB...'
        }
    })

@app.route('/compare', methods=['POST'])
def compare_faces() -> Dict[str, Any]:
    """Compare deux visages
    ---
    tags:
      - Face Comparison
    consumes:
      - application/json
    parameters:
      - in: body
        name: body
        description: Face comparison data
        required: true
        schema:
          id: face_comparison
          required:
            - image1
            - image2
          properties:
            image1:
              type: string
              description: Base64 encoded image with first face
            image2:
              type: string
              description: Base64 encoded image with second face
    responses:
      200:
        description: Comparison result
        schema:
          id: comparison_result
          properties:
            success:
              type: boolean
            match:
              type: boolean
            distance:
              type: number
              format: float
            threshold:
              type: number
              format: float
            similarity:
              type: number
              format: float
      400:
        description: Error in comparison
        schema:
          id: error_response
          properties:
            success:
              type: boolean
            error:
              type: string
    """
    try:
        data: Dict[str, Any] = request.get_json()
        image1_base64: str = data['image1'].split(',')[1]
        image2_base64: str = data['image2'].split(',')[1]

        # Décodage des images
        image1_bytes: bytes = base64.b64decode(image1_base64)
        image2_bytes: bytes = base64.b64decode(image2_base64)

        image1_array: np.ndarray = np.frombuffer(image1_bytes, dtype=np.uint8)
        image2_array: np.ndarray = np.frombuffer(image2_bytes, dtype=np.uint8)

        image1: np.ndarray = cv2.imdecode(image1_array, cv2.IMREAD_COLOR)
        image2: np.ndarray = cv2.imdecode(image2_array, cv2.IMREAD_COLOR)

        rgb_image1: np.ndarray = cv2.cvtColor(image1, cv2.COLOR_BGR2RGB)
        rgb_image2: np.ndarray = cv2.cvtColor(image2, cv2.COLOR_BGR2RGB)

        # Extraction des encodages
        encodings1: List[np.ndarray] = face_recognition.face_encodings(rgb_image1)
        encodings2: List[np.ndarray] = face_recognition.face_encodings(rgb_image2)

        if len(encodings1) == 0 or len(encodings2) == 0:
            return jsonify({
                'success': False,
                'error': 'One or both images have no face'
            })

        # Comparaison
        results: List[bool] = face_recognition.compare_faces(
            [encodings1[0]],
            encodings2[0],
            tolerance=0.5
        )
        distance: float = face_recognition.face_distance(
            [encodings1[0]],
            encodings2[0]
        )[0]

        return jsonify({
            'success': True,
            'match': bool(results[0]),
            'distance': float(distance),
            'threshold': 0.5,
            'similarity': float(1 - distance)
        })

    except Exception as e:
        return jsonify({
            'success': False,
            'error': str(e)
        })

if __name__ == '__main__':
    app.run(host='0.0.0.0', port=5000, threaded=True, debug=True)