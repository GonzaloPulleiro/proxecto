# Proyecto fin de ciclo

### Benvid@! 
#### Son Gonzalo Pulleiro, alumno de DAM e propóñome a realizar un <abbr title="Proxecto Fin de Ciclo">PFC</abbr> entregable, o cal vai a consistir nunha aplicación de xestión de recetas de cociña,   ***"Cómo Como"***.
> ___
> ![icono_app](doc/img/Capturas%20App/pantalla_dispositivo.png)
> 
> Esta aplicación,  consistirá nunha app móbil para o **Sistema Operativo Android**,
>  na cal empregarei a linguaxe **Kotlin** para o código, e o <abbr title="Entorno de Desenvolvemento Integrado">IDE</abbr>
>  **Android Studio** para desenvolver a aplicación.    
>

## Descripción

> A aplicación contará con algunhas recetas incluídas na base de datos, das que poderás visualizar:
> - unha imaxe,
> - os seus ingredientes e as cantidades destes,
> - a elaboración da receta.
> 
> O usuario terá a opción de engadir as súas propias recetas. Solicítarase a introducción dos ingredientes, as cantidades, a elaboración e unha imaxe. 
> 
> Levarase a cabo unha xestión de usuarios, na cal existirá un usuario administrador. 
> 
> Este, terá as mesmas opcións que un usuario corrente:
> - crear recetas,
> - editalas,
> - eliminalas,
> - engadir recetas a un listado de favoritos,
> - xerar un menú semanal,
> 
>  a maiores poderá eliminar outros usuarios existentes.
>
> 
> Con todas as recetas dispoñibles poderase organizar un _menú semanal_, e tamén visualizará as recetas que se marquen como _favoritas_, para poder acceder a elas máis facilmente. 
>
> 
> Para levar a cabo esta aplicación, empregarei a linguaxe **Kotlin**, xa que é a linguaxe oficial para _Android_
> desde que foi anunciado por Google no ano 2017. 
> 
> Como xestor de base de datos escollin **SQLite**. Dada a natureza da miña app, **SQLite** ven integrado en _Android_, de código aberto, 
> de tamaño reducido, permite o funcionamento da app sen conexión a Internet, a base de datos está contida dentro da app, sen necesidade de empregar un servidor, 
> é fácil de empregar e con alto rendemento, ademais de pola persistencia de datos, unha vez cerres a app ou reinicies o dispositivo.
>
>A base de datos foi creada en **DBeaver**, unha ferramenta de código aberto e _multiplataforma_ que soporta gran variedade de bases de datos,. Conta cunha interfaz simple e intuitiva, 
>que simplifica a creación da base de datos e a incorporación de datos á mesma. Xerado o arquivo, simplemente se creou un paquete dentro do proxecto chamado ***assets***,
>no cal se incluiu.

## Instalación / Puesta en marcha

> A app pódese instalar no dispositivo _Android_ das seguintes maneiras:
>
> 1. Instalación mediante **Android Studio** .
> 2. Instalación mediante un **APK**.
>
> Para a primeira maneira sería preciso:
>
> - Habilitar o ***modo desarrollador*** no dispositivo. (_Ajustes > Acerca del teléfono e pulsar varias veces sobre "Número de compilación"_. Despois acceder a _Opciones de desarrollador_ en _Ajustes_ e activar a opción _"Depuración por USB"_.)
> - Conectar o dispositivo ao PC. (_Seleccionar a opción de "Transferir archivos" no dispositivo_).
> - Abrir o proxecto en _Android Studio_, seleccionar o dispositivo conectado e executar a app no botón _"Run"_.
>
> Para a segunda:
>
> - No dispositivo > _Ajustes > Seguridad > activar "Instalar apps desde fuentes desconocidas"_ e aquí activamos a opción de _Administrador de archivos_.
>  
> Isto dependerá da versión de cada dispositivo. De non ser capaz a activar desta maneira, probar a -> _Ajustes_ > Buscar mediante: _"Instalar apps desde fuentes desconocidas"_ e a partir de aquí seguir o resto de pasos.
> - A app está compartida aquí:  [Cómo Como](https://drive.google.com/file/d/1Da1_i35HH3dl-YrOaDPx7j1loZjIdBOE/view?usp=drive_link).
> - O usuario terá que acceder a el e autorizar a descarga do arquivo _APK_.
> - Acto seguido, dirixirase a _Administrador de archivos_ no dispositivo. _APK > Seleccionar o arquivo _APK_ descargado > "Instalar" > "Abrir"_.
>
>Para máis axuda > [Instalación APK](https://www.youtube.com/shorts/gk8HCbpTRV8).
>
> No aspecto de posta en marcha,  unha vez iniciada a app, esta en sí é intuitiva.
> 
> O usuario deberá rexistrarse ou iniciar sesión, e acto seguido comezaría a visualizar por completo as funcións da aplicación.

## Uso

> ***Cómo Como*** está desenvolvida en fragmentos. Intentouse realizar unha aplicación sinxela e intuitiva, para facilitar a usabilidade da mesma e o rápido aprendizaxe do usuario.

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

Para máis información podes consultar o seguinte video [Uso Cómo Como](https://youtu.be/Dnto4OV8YM8).

Ou tamén o seguinte [manual](doc/diagramas/Manual_Usuario.pdf).
## Sobre el autor

> Son Gonzalo Pulleiro Juncal, de Ordes (A Coruña). Son graduado en Xeografía e Ordenación do Territorio pola USC, e despois de máis de 6 anos traballando no mundo do comercio, decidín darlle un xiro á miña vida laboral e formarme no desenvolvemento de aplicacións multiplataforma, algo que estou (agardo) a piques de lograr.  
>
> 
> Considerome unha persoa aplicada, e decidida, que busca acadar as metas que se propón.
> En canto a tecnoloxía que máis domine, diría que Java, posto que é o fundamental deste FP, SQL, e pode que Python.  
>
> 
> Decanteime pola realización deste proxecto por aportar algo á comunidade que non sexa de pago, a meta principal é realizar unha aplicación fácil e intuitiva, sinxela de usar, e que aporte ideas novas á comunidade.  
>
> 
> Podese contactar conmigo a través do correo [gonzalopju@gmail.com](mailto:gonzalopju@gmail.com) ou a través do número telefónico [665013395](tel:+34665013395).  

## Licencia

> ***Copyright(&copy;)*** 2024 Gonzalo Pulleiro Juncal. 
>
> Concédese permiso para copiar, distribuír e/ou modificar este documento baixo os termos da Licenza de Documentación Libre GNU, Versión 1.3 ou calquera versión posterior publicada pola Free Software Foundation.
>
>
>Sen seccións invariables, sen textos de portada e sen textos de contraportada.
>
>Inclúese unha copia da licenza na sección titulada "GNU Free Documentation License".
 [License](LICENSE.md)


## Índice

1. Anteproxecto
    * 1.1. [Idea](doc/templates/1_idea.md)
2. [Análise](doc/templates/2_analise.md)
3. [Planificación](doc/templates/3_planificacion.md)
4. [Deseño](doc/templates/4_deseño.md)
5. [Implantación](doc/templates/5_implantacion.md)


## Guía de contribución

> Para contribuír podes:
>
> * Propoñer novas funcionalidades.
> * Corrección e optimización do código.
> * Melloras na interfaz.
> * Desenvolvemento de test automatizados. 
> * Documentación.
>
> Como podes facer?
>
> 1. Clona o repositorio con:
>
>     `git clone https://gitlab.iessanclemente.net/damd/a22gonzalopj`
> 
> 2. Configura o IDE. Neste caso _Android Studio_.
> 
> 3. Crea a túa rama.
> 
>     `git branch nome-rama`
> 
>     `git checkout nome-rama`
> 
> 4. Fai modificacións e aporta.
>
>     `git commit -m "nome do commit"`
>
>     `git push origin nome-rama`
>
>
> #### Contacto
> Se tes algunha dúbida, contacta comigo:
>
> [gonzalopju@gmail.com](mailto:gonzalopju@gmail.com) 
> ___
> [665013395](tel:+34665013395)  
>___

## Links

> [**Kotlin**](https://kotlinlang.org/) : Linguaxe oficial _Sistema Operativo Android_ dende 2017.
> 
> [**IDE Android Studio**](https://developer.android.com/studio?hl=es-419) : Entorno de desenvolvemento oficial para _Android_.
>
> [**Android**](https://www.android.com/) : Páxina do sistema operativo.
>
> [**Android Developer**](https://developer.android.com/develop?hl=es-419) : Exemplos, ideas, axuda...
>
> [**Jetpack Compose**](https://developer.android.com/compose) : Simplificación deseño interfaz usuario.
> 
> [**Material Design**](https://m3.material.io/) : Ideas deseño para app.
> 
> [**SQLite en Kotlin**](https://www.geeksforgeeks.org/android-sqlite-database-in-kotlin/) : Empregar SQLite con Kotlin.
>
> [**DBeaver**](https://dbeaver.io/) : Programa para xestión da base de datos.
>
> [**SQLite**](https://www.sqlite.org/) : Linguaxe da base de datos.
>
> [**Flaticon**](https://www.flaticon.es/) : Páxina da que obtiven os iconos empregados na app.
>
> [**Miro**](https://miro.com/index/) : Páxina para realización do Mockup.
>
> [**UMLet**](https://www.umlet.com/) : Ferramenta UML Case.
>
> [**VisualParadigm**](https://www.visual-paradigm.com/) : Ferramenta UML Case.
> 
> [**BCrypt**](https://jlcondori.com/entendiendo-bcrypt-en-node-js-hashing-y-seguridad-basica/) : Encriptado para gardar contraseñas.