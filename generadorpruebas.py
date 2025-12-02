import random

def generar_entrada(nombre_archivo, T=1, max_n=1000, max_coord=100000):
    with open(nombre_archivo, "w") as f:
        f.write(f"{T}\n")
        
        for _ in range(T):
            n = max_n
            usados = set()
            puntos = []
            
            while len(puntos) < n:
                x = random.randint(1, max_coord)
                y = random.randint(1, max_coord)
                if (x, y) not in usados:
                    usados.add((x, y))
                    puntos.append((x, y))
            
            # Construir la línea del caso
            linea = str(n)
            for (x, y) in puntos:
                linea += f" {x} {y}"
            f.write(linea + "\n")

# Ejemplo de uso:
generar_entrada("input_random2.txt")  # Genera 50 casos de prueba