# FASE DE IMPLANTACIÓN

## Manual técnico:

### Información relativa á instalación: 

1. Requisitos Técnicos.
   
   * O usuario deberá dispor de:
   1. Smartphone con _Sistema Operativo Android versión 5.0 _Lollipop_ ou superior.
   2. A lo menos 2 GB de RAM.

2. Configuración previa do dispositivo e instalación da aplicación.
   1. A app pódese instalar no dispositivo _Android_ das seguintes maneiras:
      1. Instalación mediante **Android Studio** .
      2. Instalación mediante un **APK**.
>
> Para a primeira maneira sería preciso:
>
> - Habilitar o ***modo desarrollador*** no dispositivo. (_Ajustes > Acerca del teléfono e pulsar varias veces sobre "Número de compilación"_. Despois acceder a _Opciones de desarrollador_ en _Ajustes_ e activar a opción _"Depuración por USB"_.)
> - Conectar o dispositivo ao PC. (_Seleccionar a opción de "Transferir archivos" no dispositivo_).
> - Abrir o proxecto en _Android Studio_, seleccionar o dispositivo conectado e executar a app no botón _"Run"_.
>
> Para a segunda:
> - No dispositivo > _Ajustes > Seguridad > activar "Instalar apps desde fuentes desconocidas"_ e aquí activamos a opción de _Administrador de archivos_.
> - A app está compartida aquí:  [Cómo Como](https://drive.google.com/file/d/1Da1_i35HH3dl-YrOaDPx7j1loZjIdBOE/view?usp=drive_link).
> - O usuario terá que acceder a el e autorizar a descarga do arquivo _APK_.
> - Acto seguido, dirixirase a _Administrador de archivos_ no dispositivo. _APK > Seleccionar o arquivo _APK_ descargado > "Instalar" > "Abrir"_.
> 
>Para máis axuda > [Instalación APK](https://www.youtube.com/shorts/gk8HCbpTRV8).
>

### Despregue da aplicación:

Para xerar un APK firmado seguiuse os seguintes pasos:

   1. _Android Studio > Build > Generate Signed App Bundle/APK..._
   2. Na seguinte pantalla seleccionamos _APK_ e pulsamos _Next_.
   3. Cubrimos os campos que se solicitan e volvemos pulsar _Next_.
   4. Escollemos _release_ na seguinte pantalla e pulsamos sobre _Create_.
   5. O finalizar todo o proceso, Android Studio informaríanos de que o apk está xerado.
   6. O ***APK*** estaría localizado aquí -> [APK](../../ComoComo/app/release/app-release.apk).

### Información relativa ó mantemento do sistema: 

Unha vez que ***Cómo Como*** está instalada no dispositivo, non é preciso facer tarefas de administración, tanto a nivel de usuarios como do programa en si mesmo.

Como mellora futura, poderíase implementar unha opción na que o usuario puidese facer un backup dos datos gardados na aplicación.

No que respecta á xestión da seguridade, ***Cómo Como*** non permite o acceso á base de datos é polo tanto a súa integridade estaría garantida.

## Xestión de incidencias

Unha vez xerado o apk e posto no repositorio, non habería ningún tipo de incidencia na aplicación, porque xa estaría probada antes da súa publicación no repositorio como xa se explicou no apartado correspondente.O único problema que podería ocorrer é que segundo a versión de Android que tivese o usuario, non soubese habilitar, no móbil ou tablet, a instalación de aplicacións descoñecidas (non descargadas de _Google Play_).

Para este contratempo o usuario poderá contactar con [gonzalopju@gmail.com](mailto:gonzalopju@gmail.com) co propósito de resolver esta incidencia.

## Protección de datos de carácter persoal.

Dado que ***Cómo Como*** non recompila ningún tipo dato de carácter persoal (localización, ip, etc.), non se está obrigado a cumprir coas dúas normativas básicas relativas á protección de datos de carácter persoal:

- [Regulamento (UE) 2016/679 do Parlamento Europeo e do Consello, do 27 de abril de 2016, relativo á protección das personas físicas no que respecta ao tratamento de datos personais e á libre circulación destos datos](https://www.boe.es/buscar/doc.php?id=DOUE-L-2016-80807)

- [Lei Orgánica 3/2018, do 5 de decembro, de Protección de Datos Personais e garantía dos dereitos dixitais.](https://www.boe.es/buscar/act.php?id=BOE-A-2018-16673)

## Manual de usuario

[Manual de usuario](../diagramas/Manual_Usuario.pdf)



