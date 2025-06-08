Servicios
### Servicios

#### OrganizationService
Maneja las operaciones relacionadas con organizaciones, como listar, crear, obtener por ID y eliminar.

#### ApplicationService
Maneja las operaciones relacionadas con aplicaciones, como listar todas las aplicaciones, obtener aplicaciones por organización, crear una nueva aplicación asociada a una organización y eliminar aplicaciones.

### Controladores

#### OrganizationController
Expone endpoints para manejar organizaciones:
- **GET** `/api/organizations`: Lista todas las organizaciones.
- **GET** `/api/organizations/{id}`: Obtiene una organización por su ID.
- **POST** `/api/organizations`: Crea una nueva organización.
- **DELETE** `/api/organizations/{id}`: Elimina una organización por su ID.

#### ApplicationController
Expone endpoints para manejar aplicaciones:
- **GET** `/api/applications`: Lista todas las aplicaciones.
- **GET** `/api/applications/{id}`: Obtiene una aplicación por su ID.
- **GET** `/api/applications/organization/{organizationId}`: Lista las aplicaciones de una organización específica.
- **POST** `/api/applications/organization/{organizationId}`: Crea una nueva aplicación asociada a una organización.
- **DELETE** `/api/applications/{id}`: Elimina una aplicación por su ID.

### Imagen de contenedor

Se puede generar una imagen de contenedor nativa con la tarea `bootBuildImage` de Spring Boot.

Variables de entorno útiles:

- `IMAGE_NAME`: nombre completo de la imagen (incluye el repositorio). Por defecto se usa `rapid-config-server:${version}`.
- `REGISTRY_URL`: URL del registro al que se publicará la imagen.
- `REGISTRY_USERNAME` y `REGISTRY_PASSWORD`: credenciales para dicho registro.

Ejemplo de ejecución:

```bash
IMAGE_NAME=tu-registro.com/rapid-config-server:1.0 \
REGISTRY_URL=tu-registro.com \
REGISTRY_USERNAME=usuario REGISTRY_PASSWORD=clave \
./gradlew bootBuildImage
```

Si se define `REGISTRY_URL`, la tarea publicará la imagen automáticamente en ese repositorio.
