#기본 길찾기 - 실제 좌표
import osmnx as ox
import networkx as nx

# 지정한 위치의 도로 네트워크 로드
location = 'Songdo, Yeonsu, Incheon, South Korea'
G = ox.graph_from_place(location, network_type='walk') 

# 시작점과 목표점의 좌표
start = (37.3733, 126.6326) # 예: 인천대학교
end = (37.3785, 126.6326) # 예: 인입역

# 가장 가까운 노드 찾기
start_node = ox.distance.nearest_nodes(G, start[1], start[0])
end_node = ox.distance.nearest_nodes(G, end[1], end[0])

# A* 알고리즘으로 최단 경로 찾기
route = nx.astar_path(G, start_node, end_node, weight='length')

buffer = 0.003  # 이 값을 조절하여 여유분을 설정할 수 있습니다.

north = max(start[0], end[0]) + buffer
south = min(start[0], end[0]) - buffer
east = max(start[1], end[1]) + buffer +0.002
west = min(start[1], end[1]) - buffer

# 경로를 지도 위에 그리기
fig, ax = ox.plot_graph_route(G, route, bbox=(north, south, east, west))
