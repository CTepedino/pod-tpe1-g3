# POD-TPE1-G3

## Autores

- Bloise, Luca
- Mendonca, Juana
- Tepedino, Cristian

## Compilación

Para compilar el proyecto, se deben ejecutar el siguientes comando desde la carpeta raiz del proyecto:

```bash
mvn clean install
```

Esto generara los directorios "target" en client y en server, y dentro se encontrara el archivo "tpe1-g3-{directorio}-2024.2Q-bin.tar.gz". 

Descomprimir:

```bash
tar -xvzf tpe1-g3-{directorio}-2024.2Q-bin.tar.gz  
```

Dentro, se encuentran los scripts necesarios para correr los clientes y el servidor respectivamente.

## Uso

### Servidor

Para iniciar el servidor, ejecutar el siguiente comando:

```bash
./run-server.sh
```

Por defecto, el servidor estara escuchando en el puerto 50051. Para cambiarlo, se puede usar el argumento

```bash
-DserverPort={port}
```

### Clientes

Para ejecutar un cliente, ejecutar el comando asociado al respectivo cliente:

```bash
./administrationClient.sh
```

```bash
./waitingRoomClient.sh
```

```bash
./emergencyCareClient.sh
```

```bash
./doctorPagerClient.sh
```

```bash
./queryClient.sh
```

En todos los casos, se deberan utilizar los siguientes argumentos:

- Para indicar la dirección y puerto del servidor:
```bash
-DserverAddress={address:port}
```
- Para indicar la acción a realizar en el cliente: 
```bash
-Daction={action}
```

Ademas, según la acción a realizar, se podrian requerir los siguientes paramentros:

- Para indicar un doctor
```bash
-Ddoctor={name}
```

- Para indicar el nivel de un paciente o de un doctor:
```bash
-Dlevel={level}
```

- Para indicar la disponibilidad de un doctor: 
```bash
-Davailability={available|unavailable}
```

- Para indicar un paciente:
```bash
-Dpatient={name}
```

- Para indicar un consultorio:
```bash
-Droom={number}
```

- Para indicar el path del archivo de salida:
```bash
-DoutPath={path}
```
