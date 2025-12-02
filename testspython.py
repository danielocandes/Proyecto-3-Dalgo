import bisect
def normal_greedy(n:int, focos:list):
    coordsY = {}
    coordsX = {}
    
    for x,y in focos:
        if coordsY.get(y) is None:
            coordsY[y] = []
        if coordsX.get(x) is None:
            coordsX[x] = []
        
        coordsY[y].append(x)
        coordsX[x].append(y)
    
    horizontales = {}
    verticales = {}

    for cy in coordsY:
        horizontales[cy] = []
        focosEnFila: list = coordsY[cy]
        focosEnFila.sort()
        lineaRepre = (focosEnFila[0], focosEnFila[-1])
        horizontales[cy].append(lineaRepre)
    
    keysHorizontales = sorted(horizontales.keys())
    
    for cx in coordsX:
        verticales[cx] = []
        focosEnCol: list = coordsX[cx]
        focosEnCol.sort()
        
        start = focosEnCol[0]
        i = 1
        while i < len(focosEnCol):
            # se intenta crear una linea hacia el proximo foco
            y_objetivo = focosEnCol[i]
            bloqueo = primer_bloqueo_vertical(cx, start, y_objetivo, horizontales, keysHorizontales, coordsX)

            if bloqueo is None:
                # no hay bloque aun, se puede hacer esa linea y se sigue revisando en el while si se puede extender mas
                i += 1
            else:
                # hay un bloqueo si intentamos extender la linea a y=focosEnCol[i]
                # entonces la linea actual la llevamos solo hasta y=focosEnCol[i-1]
               # if focosEnCol[i-1] != start:
                lineaRepre = (start, focosEnCol[i-1])
                verticales[cx].append(lineaRepre)
                
                # comenzamos a crear una nueva linea que comienza en focosEnCol[i], asi evitando el bloqueo
                start = focosEnCol[i]
                i += 1
        
        verticales[cx].append(( start, focosEnCol[-1]))
    
    return horizontales, verticales
    

def primer_bloqueo_vertical(x, y1, y2, horizontales, keysHorizontales, coordsX):
    """
    Retorna:
        - None → no hay bloqueo
        - y_bloqueo → la coordenada Y exacta donde hay una intersección prohibida
    """

    # encontrar primeros y existentes >= y1
    start = bisect.bisect_left(keysHorizontales, y1)

    for idx in range(start, len(keysHorizontales)):
        y = keysHorizontales[idx]
        if y > y2:
            break

        # revisar si en esta fila horizontal hay intersección
        for (x1, x2) in horizontales[y]:
            # intersección: el vertical en x cruza la horizontal en y
            if x1 <= x <= x2:
                # solo es problematica la interseccion si no hay un foco ahi
                if y not in coordsX[x]:
                    return y  # esta linea con coordenada 'y' bloquea!

    return None



def run_from_file(path: str):
    """
    Lee un archivo .txt donde hay números representando focos:
    x1 y1 x2 y2 x3 y3 ...

    Retorna:
        horizontales, verticales  (lo que retorne tu greedy)
    """

    # Leer todo el archivo
    with open(path, "r") as f:
        contenido = f.read()

    # Extraer números como enteros
    numeros = list(map(int, contenido.split()))

    if len(numeros) % 2 != 0:
        raise ValueError("El archivo no tiene un número par de enteros (faltan coordenadas).")

    # Convertir a lista de tuplas (x, y)
    focos = []
    for i in range(0, len(numeros), 2):
        x = numeros[i]
        y = numeros[i+1]
        focos.append((x, y))

    n = len(focos)

    # Llamar tu función greedy
    horizontales, verticales = normal_greedy(n, focos)
    print_output_format(horizontales, verticales)

def print_output_format(horizontales, verticales):
    # ---- HORIZONTALES ----
    # Cada entrada: y -> [(x1, x2), (x3, x4), ...]
    
    horiz_list = []
    for y in horizontales:
        for (x1, x2) in horizontales[y]:
            horiz_list.append((x1, y, x2, y))
    
    # Ordenar solo para consistencia (no obligatorio)
    horiz_list.sort()

    # imprimir
    print(len(horiz_list), end=" ")
    for (x1, y1, x2, y2) in horiz_list:
        print(x1, y1, x2, y2, end=" ")
    print()  # salto de línea

    # ---- VERTICALES ----
    # Cada entrada: x -> [(y1, y2), ...]
    
    vert_list = []
    for x in verticales:
        for (y1, y2) in verticales[x]:
            vert_list.append((x, y1, x, y2))
    
    # Ordenar solo para consistencia (no obligatorio)
    vert_list.sort()

    print(len(vert_list), end=" ")
    for (x1, y1, x2, y2) in vert_list:
        print(x1, y1, x2, y2, end=" ")
    print()  # salto de línea final

run_from_file("ejemplo.txt")