#장애물 피하기 길찾기 - 그리드
import math
import matplotlib.pyplot as plt

def a_star_algorithm(start, goal, obstacles):
    # 초기 설정
    open_list = [start]
    closed_list = []
    g = {}
    parents = {}

    # 비용 설정
    g[start] = 0
    parents[start] = start

    while open_list:
        current = min(open_list, key=lambda x:g[x] + heuristic(x, goal))

        # 목표점에 도달하면 경로 반환
        if current == goal:
            path = []
            while current != start:
                path.append(current)
                current = parents[current]
            path.append(start)
            return path[::-1]

        open_list.remove(current)
        closed_list.append(current)

        for neighbor in neighbors(current):
            if neighbor in closed_list or neighbor in obstacles:
                continue
            if neighbor not in open_list:
                open_list.append(neighbor)

            tentative_g = g[current] + distance(current, neighbor)

            if neighbor in g and tentative_g >= g[neighbor]:
                continue

            parents[neighbor] = current
            g[neighbor] = tentative_g

    return None  # 목표점에 도달할 수 없는 경우

def distance(a, b):
    # 실제 좌표에 대한 거리 계산 (유클리드 거리)
    return math.sqrt((a[0] - b[0])**2 + (a[1] - b[1])**2)

def heuristic(a, b):
    # 실제 좌표에 대한 휴리스틱 함수 (직선 거리)
    return distance(a, b)

def neighbors(node):
    # 주변 좌표 반환
    directions = [(0, 1), (0, -1), (1, 0), (-1, 0), (1, 1), (1, -1), (-1, 1), (-1, -1)]
    result = [(node[0] + i[0], node[1] + i[1]) for i in directions]
    return result

#---------------------------------main-----------------------------------------------#

# 시작점, 목표점, 장애물 설정
start = (0, 0)
goal = (10, 10)
obstacles = [(3, 3), (3, 4), (7, 6), (4, 4)]

# A* 알고리즘으로 경로 찾기
path = a_star_algorithm(start, goal, obstacles)

# 결과 시각화
if path is not None:
    # 경로 그리기
    x_path = [point[0] for point in path]
    y_path = [point[1] for point in path]
    plt.plot(x_path, y_path, 'o-', label='Path')

    # 장애물 표시
    x_obstacles = [point[0] for point in obstacles]
    y_obstacles = [point[1] for point in obstacles]
    plt.plot(x_obstacles, y_obstacles, 's', color='red', label='Obstacles')

    # 시작점, 목표점 표시
    plt.plot(start[0], start[1], 'go', label='Start')
    plt.plot(goal[0], goal[1], 'bo', label='Goal')

    plt.grid(True)
    plt.legend()
    plt.show()
else:
    print("경로를 찾을 수 없습니다.")