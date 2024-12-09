# REQUIRIMENTOS DO SISTEMA
Este documento describe os requirimentos para ***CÓMO COMO***, especificando que funcionalidade ofrecerá e de que xeito.

## Descrición Xeral

O proxecto consistirá nunha aplicación de xestión de recetas, mediante a cal se pode acceder a un listado de recetas que xa veñen incluídas na base de datos, 
engadilas a favoritas, engadilas a un menú semanal, subir máis recetas, modificalas e eliminalas. 

## Funcionalidades

1. Xestión de usuarios
   1. O sistema xestionará a alta e modificación de usuarios.
   2. Existirá un usuario administrador que poderá eliminar usuarios.
2. Xestión de recetas ligada ao perfil.
   1. O sistema xestionará	a alta, baixa, modificación e consulta de recetas existentes do usuario activo.
   2. O sistema proporcionará filtrado, ordenación e búsqueda de recetas. 
3. Xestión de favoritos.
   1. O sistema xestionará a alta e baixa de recetas engadidas a favoritas.
4. Xestión de menú semanal.
   1. O sistema xestionará a alta e baixa das recetas incluidas no menú semanal.
 
## Requerimentos non funcionais
1. Cumplirase o estándar WAI nivel A.
2. O sistema será desenvolvido para Android.
3. O sistema empregará API 21.

## Tipos de usuarios
O sistema terá dous tipos de usuarios.

  * Usuario simple, que poderá crear un menú semanal, engadir recetas a favoritas, subir recetas, editalas ou modificalas. 
  * Usuario administrador, que terá as mesmas funcións que un usuario simple, e a maiores poderá eliminar outros usuarios.


## Avaliación da viabilidade técnica do proxecto

### Hardware requerido
- Para desenvolver:

    Un ordenador con capacidade para executar _Android Studio_, con polo menos, 8 GB de RAM e un procesador de 64 bits, posto que _Android Studio_ é bastante esixente en recursos.

    Un dispositivo móvil con _Sistema Operativo Android_ para proceder coa realización das probas.

- Usuario:

    Smartphone con _Sistema Operativo Android versión 5.0 _Lollipop_ ou superior.

    A lo menos 2 GB de RAM.

### Software

- Sistema operativo de desenvolvemento: 
  - Windows 11.
- Entorno de desenvolvemento:
  - Android Studio. 
- Linguaxe de programación:
  - Kotlin.
- Base de datos:
  - SQLite.

## Interfaces externos

### Interfaces de usuario

1. **Pantalla inicial**
   1. Opción de _Iniciar Sesión_ ou _Rexistrarse_.
   2. Para rexistrarse > Nome, Email (_verificación de que sexa un email_), Contrasinal e confirmación de contrasinal (_mínimo 8 caracteres: maiúsculas, minúsculas, número e caracter especial_).
      1. Poderán existir varios usuarios co mesmo nome, pero non co mesmo contrasinal.
      2. Só poderá existir un mesmo email na base de datos.
      3. Só poderá existir un usuario administrador, para iso, rexistrarase co nome ***"admin"***.
   3. Para iniciar sesión > Nome e Contrasinal.
2. **Pantalla inicio**
   1. Visualización do nome da app, un icono de información (_toolTipText de información da app_), imaxe e barra inferior de navegación mediante a cal navegaremos polos fragmentos principais da aplicación e que estará visible en toda a aplicación exceptuando a pantalla inicial, indicando mediante un cambio de icono, en que pantalla nos atopamos actualmente.
3. **Pantalla recetas**
   1. Visualización dun listado de recetas a doble columna con imaxe, nome e tempo de preparación.
    Opción de ordenar o listado por defecto, buscar receta por nome, ordenar de máis a menos tempo de preparación, de menos a máis tempo de preparación, ou ordenar alfabéticamente.
   2. Si usuario selecciona unha receta pasamos a un novo fragmento:
      1. Visualizarase a receta completa, coa sua imaxe, nome, tempo de preparación, listado de ingredientes e cantidades, e a preparación desta.
      2. Faráse visible unha ***toolbar*** na parte superior da pantalla coas seguintes opcións:
         1. Volver atrás.
         2. Engadir receta a listado de favoritos.
         3. Engadir receta a menú semanal.
4. **Pantalla Menú**
   1. Visualización dun calendario (_datepicker_) co día actual marcado.
   2. Accesible visualización ate unha semana antes do día actual.
   3. Si usuario selecciona un día pasamos a un novo fragmento:
      1. Visualizarase o día seleccionado e verase as recetas engadidas para ese día e para cada comida do día (_Almorzo, Xantar, Snack ou Cea_).
      2. ***Toolbar*** con opción de volver atrás visible.
      3. Si usuario pulsa sobre receta veremos a receta completa ao detalle.
5. **Pantalla Favoritos**
   1. Visualización da cantidade total de recetas engadidas a favoritos.
   2. Recetas favoritas, con imaxe, nome e tempo de preparación. 
   3. Si usuario pulsa sobre receta veremos a receta completa ao detalle.
6. **Pantalla Perfil**
   1. Texto indicativo de perfil de usuario (P.ex: ***Perfil de Gonzalo***).
   2. Se usuario normal:
      1. Opción de ver as súas recetas:
         1. Texto indicativo de recetas compartidas.
         2. Opcións de subir, editar ou elminar receta.
      2. Opción de cerrar sesión.
   3. Se usuario administrador:
      1. A maiores do incluído para usuario normal, visualizará un icono que indica que é administrador.
      2. Opción de eliminar usuarios.


### Interfaces hardware
Esta aplicación non require integración con hardware externo como lectores de códigos 
de barras ou sensores. Limítase ao uso táctil na pantalla do dispositivo.

### Interfaces software
Nesta fase non se empregan servizos na nube. Para versións futuras podese considerar, co 
fin de facer pública de cara aos usuarios, e non só en dispositivo local.

## Análise de riscos e interesados
Como riscos.

Posibles problemas que poidan xurdir e como xestionalos:

- Compatibilidade con diferentes versións _Android_.
  - Probar a app en varios dispositivos con diferentes versións de _Android_.
- Erros na base de datos.
  - Probar á hora de insertar usuarios, eliminalos, recetas, etc, que funcione correctamente.

Como interesados.

- **Gonzalo Pulleiro Juncal**, como creador desta app son o principal interesado en que funcione correctamente e coa finalidade coa que se creou.
- **Usuario final**. Interesados en xestionar e organizar as recetas.
- **IES San Clemente**. Centro evaluador do proxecto, posto que é parte do obxectivo académico.

## Actividades
Pasos a seguir para levar a cabo o proxecto:

1. Análise de Requerimentos.
   1. Requerimentos funcionais
   2.  Requerimentos non funcionais  
2. Elaboración de diagramas.
   1. Diagrama de Gantt
   2. Diagrama entidad relación
   3. Diagrama de clases
3. Elaboración da base de datos.
   1. Creación da base de datos.
   2. Implantación de diferentes recetas para incluir de base.
4. Diseño interfaz.
5. Xestión de usuarios.
   1. Alta, modificación e eliminación
   2. Usuario administrador
6. Xestión de recetas.
   1. Alta, baixa, modificación e consulta
   2. Visualización de recetas
   3. Filtrado e ordenado
7. Xestión de favoritos.
   1. Alta e baixa
8. Xestión de menú semanal.
   1. Alta, baixa e consulta
9.  Probas de aplicación.
    1.  Probas do funcionamento da aplicación
10. Entrega proxecto.


## Melloras futuras
* Incluir a base de datos nun servidor e poder compartir a aplicación con outros usuarios, e non tan só no dispositivo local.
* Elaboración automática dunha lista da compra en función das recetas incluidas no menú semanal.
* Incluír máis recetas na base de datos, para que o usuario teña máis onde escoller.
* Búsqueda por ingredientes. 



