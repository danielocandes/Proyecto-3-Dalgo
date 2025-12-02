import bisect

def construir_horizontales(coordsY):
    horizontales = {}
    for cy in coordsY:
        focosEnFila = sorted(coordsY[cy])
        horizontales[cy] = [(focosEnFila[0], focosEnFila[-1])]
    return horizontales

def construir_verticales(coordsX, horizontales):
    verticales = {}

    keysHorizontales = sorted(horizontales.keys())

    for cx in coordsX:
        verticales[cx] = []
        focosEnCol = sorted(coordsX[cx])

        start = focosEnCol[0]
        i = 1

        while i < len(focosEnCol):
            y_objetivo = focosEnCol[i]
            bloqueo = primer_bloqueo_vertical(cx, start, y_objetivo,
                                              horizontales,
                                              keysHorizontales,
                                              coordsX)

            if bloqueo is None:
                i += 1
            else:
                # cerrar línea hasta el foco previo
                linea = (start, focosEnCol[i-1])
                verticales[cx].append(linea)

                # nueva línea desde este foco
                start = focosEnCol[i]
                i += 1

        # cerrar la última
        verticales[cx].append((start, focosEnCol[-1]))

    return verticales

# ----------------------------------------------------
def primer_bloqueo_vertical(x, y1, y2, horizontales, keysHorizontales, coordsX):
    start = bisect.bisect_left(keysHorizontales, y1)

    for idx in range(start, len(keysHorizontales)):
        y = keysHorizontales[idx]
        if y > y2:
            break

        for (x1, x2) in horizontales[y]:
            if x1 <= x <= x2:
                if y not in coordsX[x]:   # intersección prohibida
                    return y
    return None


def normal_greedy_hv(focos):
    coordsY, coordsX = construir_mapas(focos)

    horizontales = construir_horizontales(coordsY)
    verticales = construir_verticales(coordsX, horizontales)

    return horizontales, verticales


def normal_greedy_vh(focos):
    coordsY, coordsX = construir_mapas(focos)

    # Hacemos horizontales sintéticas para no bloquear verticales al inicio
    horizontales_vacios = {}

    # primero verticales sin restricciones
    verticales = construir_verticales(coordsX, horizontales_vacios)

    # luego horizontales sin restricciones
    horizontales = construir_horizontales(coordsY)

    return horizontales, verticales

def construir_mapas(focos):
    coordsY = {}
    coordsX = {}

    for x, y in focos:
        coordsY.setdefault(y, []).append(x)
        coordsX.setdefault(x, []).append(y)

    return coordsY, coordsX


def normal_greedy_best(n, focos):
    h1, v1 = normal_greedy_hv(focos)
    h2, v2 = normal_greedy_vh(focos)

    total1 = contar_lineas(h1) + contar_lineas(v1)
    total2 = contar_lineas(h2) + contar_lineas(v2)

    if total1 <= total2:
        return h1, v1
    return h2, v2


def contar_lineas(dic):
    cnt = 0
    for k in dic:
        cnt += len(dic[k])
    return cnt


def run_from_file(path: str):
    """
    Lee un archivo .txt con el formato:
        T
        n x1 y1 x2 y2 ... xn yn
        (repetido T veces)

    Para cada caso:
        - arma la lista de focos [(x,y), ...]
        - llama normal_greedy(n, focos)
        - imprime usando print_output_format()
    """

    with open(path, "r") as f:
        lineas = f.readlines()

    if not lineas:
        raise ValueError("El archivo está vacío.")

    # Leer T (número de casos)
    try:
        T = int(lineas[0].strip())
    except:
        raise ValueError("La primera línea debe contener un entero T.")

    if len(lineas) - 1 < T:
        raise ValueError(f"Se esperaban {T} casos, pero solo hay {len(lineas)-1} líneas de casos.")

    # Procesar los T casos
    for caso_idx in range(1, T + 1):
        partes = list(map(int, lineas[caso_idx].split()))

        if len(partes) < 1:
            raise ValueError(f"Línea {caso_idx+1}: no tiene datos.")

        n = partes[0]
        coords = partes[1:]

        if len(coords) != 2 * n:
            raise ValueError(
                f"Línea {caso_idx+1}: n={n} pero hay {len(coords)} valores en coordenadas (se esperaban {2*n})."
            )

        focos = []
        for i in range(0, len(coords), 2):
            x = coords[i]
            y = coords[i+1]
            focos.append((x, y))

        # Llamar tu greedy para este caso
        horizontales, verticales = normal_greedy_best(n, focos)

        # Imprimir formato requerido
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

run_from_file("input_random2.txt")