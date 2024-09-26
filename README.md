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

Esto generara los directorios "target" en client y en server, y dentro se encontrara el archivo "tpe1-g3-{directorio}-2024.2Q-bin.tar.gz". Dentro, se encuentran los scripts necesarios para correr los clientes y el servidor respectivamente.

## Uso

### Servidor

Para iniciar el servidor, ejecutar el siguiente comando:

```bash
./run-server.sh
```

El servidor estara escuchando en el puerto 50051.

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

```bash
-DserverAddress={:port}
```
