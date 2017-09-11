# FirebaseAuthAndroid-DiaProgramador2017

Es una aplicacion en android que muestra un ejemplo de utilizacion del modulo de autenticación de firebase. Admite lo siguiente: 
- Login con Email y Contraseña (y la creacion de cuentas).
- Login con Google.
- Login con Facebook.

Fue desarrollada para ejemplificar de manera simple el uso de esta herramienta en la charla en FCyT, UADER, sede Concepción del Uruguay por el Dia del Programador 2017.

## Guia de instalacion
En este proyecto se uso *Android Studio 3.0 Canary 8*
1. Clonar o descargar el repositorio.
2. Crear un proyecto en firebase.
3. En el mismo, activar los metodos de autenticacion por Email, Google y Facebook(requiere crear una aplicacion en Facebook).
4. En el mismo, en la configuracion del proyecto, asociar la aplicacion de android.
5. Agregar el archivo *google-service.json* que se obtiene en el paso anterior a la carpeta *app*.
6. Completar en el arhcivo de recurso *strings.xml* con los datos *facebook_app_id* y *fb_login_protocol_scheme* con los correspondientes a tu aplicacion de facebook.
~~~
    <string name="facebook_app_id">COMPLETE HERE</string>
    <string name="fb_login_protocol_scheme">COMPLETE HERE</string>
~~~