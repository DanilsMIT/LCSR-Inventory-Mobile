# LCSR Inventory APP 📦

Aplicación full-stack nativa en Android diseñada específicamente para optimizar la logística, control de productos y registro de ventas del negocio local **La Casa del Cielo Raso**, migrando los apuntes tradicionales de papel y lápiz a una solución digital moderna, segura y en la nube.

## 🚀 Funcionalidades Principales

### 🔍 Buscador Inteligente en Tiempo Real
- **Funcionamiento:** Barra de búsqueda superior para filtrar productos del inventario instantáneamente.
- **Ventaja:** Filtrado avanzado insensible a mayúsculas y tildes con soporte para coincidencias parciales y multi-palabra. Búsqueda fluida y de rendimiento ultrarrápido directamente sobre los elementos renderizados (`RecyclerView`) para agilizar la atención al cliente.

### 🛒 Carrito de Pedidos y Resumen de Venta
- **Funcionamiento:** Selección interactiva (toque) de elementos del inventario que lanza un modal dinámico para definir cantidades.
- **Resumen y Cierre:** Botón inferior que indica el total matemático acumulado en tiempo real. Al presionarlo, despliega un `DialogFragment` modular en formato de ticket (producto, precio unitario, cantidad y subtotal). 
- **Gestión:** Permite eliminar productos del carrito manteniendo presionada la tarjeta, y culmina enviando la factura digital al historial de ventas.

### ➕ Gestión de Inventario (CRUD Completo)
- **Agregar Productos:** Botón flotante que despliega un formulario modal (`PopupFormProduct`) limpio para registrar artículos con validación de datos numéricos.
- **Editar:** Botón dedicado en cada tarjeta para actualizar nombres o precios en Firebase al instante.
- **Eliminar:** Protección contra borrados accidentales mediante una ventana de alerta de confirmación (`PopupAlert`) que elimina permanentemente el producto y su fotografía asociada para ahorrar espacio en la nube.

### 🖼️ Gestión Visual y Nube (Cloudinary)
- **Fotografías y Cámara:** Cada producto puede almacenar una fotografía propia. A través de un menú contextual, el usuario puede abrir la cámara del dispositivo o elegir desde la galería. Las fotografías son corregidas de rotación y comprimidas automáticamente.
- **CDN Global:** A diferencia de las arquitecturas que saturan el almacenamiento local o bases costosas, las imágenes se suben de forma asíncrona a **Cloudinary**. Se obtiene una URL segura (HTTPS) que permite que todos los dispositivos de la empresa sincronizados vean la misma foto al instante gracias al sistema inteligente de caché visual (Glide).

### 📈 Historial de Ventas Sincronizado
- **Registro de Transacciones:** Cada vez que se confirma un carrito, se genera un recibo digital estructurado en la nube con fecha y hora exactas (`#REC-ID`).
- **Navegación:** Pestaña lateral que lista todas las ventas históricas de más recientes a antiguas, permitiendo dar un toque para expandir los detalles de los productos vendidos o mantener presionado para eliminar el registro.

### 🔒 Autenticación y Seguridad (Firebase Auth)
- **Protección de Datos:** Pantallas de `LoginActivity` y `RegisterActivity` bajo el ecosistema de seguridad de **Firebase Authentication**.
- **Acceso Autorizado:** Impide que usuarios sin cuenta accedan a los catálogos o al sistema de ventas. 
- **Sesión Persistente:** Manejo automático del token de sesión para que el usuario autorizado ingrese directo a su área de trabajo sin reescribir contraseñas, e incluye funcionalidad de Cierre de Sesión seguro.

---

## 🛠️ Arquitectura y Tecnologías
El proyecto está estructurado bajo estándares profesionales de ingeniería de software para ecosistemas móviles:

- **Frontend:** Android Nativo (Java y XML).
- **Arquitectura UI:** Implementación de **ViewBinding** para una interacción segura y directa con las vistas, eliminando la sobrecarga y los crashes por referencias nulas típicos de `findViewById`.
- **Componentes Modulares:** Uso intensivo de `Fragments` (Inventario y Ventas) acoplados a una `MainActivity` contenedora con Navigation Drawer.
- **Modales Personalizados:** Extensión de `DialogFragments` con fondos transparentes y dimensiones dinámicas que respetan la experiencia UX en pantalla sin bloquear el hilo principal.
- **Backend & Base de Datos:** **Firebase Realtime Database** estructurada en NoSQL. Permite suscripciones pasivas en tiempo real (`addValueEventListener`) para mostrar actualizaciones simultáneas en cualquier teléfono sin tener que recargar la pantalla manualmente.
- **Manejo de Imágenes:** Integración con **Cloudinary SDK** para subida y borrado de *assets* en la nube, y **Glide** para carga eficiente de imágenes y gestión automática de caché de disco, reduciendo el consumo de internet móvil.

## 🛡️ UI/UX y Optimización
- **Estabilidad Visual:** Orientación de pantalla bloqueada en modo vertical (`portrait`) desde el AndroidManifest para garantizar que la jerarquía de los ViewGroups (especialmente las tablas de recibos) mantengan una integridad geométrica de lectura ágil.
- **Prevención de Memoria (Leaks):** Rutinas destructoras en métodos como `onDestroyView()` para desacoplar el ViewBinding de los fragmentos muertos, optimizando la memoria RAM del equipo.
- **Seguridad en DB (Cloud Rules):** Envío encapsulado de variables ocultas (`app_secret`) para validar las operaciones de escritura/borrado contra los endpoints públicos.