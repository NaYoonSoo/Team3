#기본 길찾기 - 실제 좌표
import osmnx as ox
import networkx as nx
import pandas as pd
import math

# 지정한 위치의 도로 네트워크 로드
location = 'Songdo, Yeonsu, Incheon, South Korea'
#G = ox.graph_from_place(location, network_type='walk') 
G = nx.MultiDiGraph()
G.graph['crs'] = 'EPSG:4326'

df = pd.read_pickle('data/node_songdo1dong.pkl')
df_link = pd.read_pickle('data/link.pkl')

def calculate_distance(node1, node2):
    # 위도와 경도를 라디안으로 변환합니다.
    lat1, lon1 = math.radians(node1['y']), math.radians(node1['x'])
    lat2, lon2 = math.radians(node2['y']), math.radians(node2['x'])
    
    # haversine 공식을 사용하여 두 좌표 사이의 거리를 계산합니다.
    dlon = lon2 - lon1
    dlat = lat2 - lat1
    a = math.sin(dlat/2)**2 + math.cos(lat1) * math.cos(lat2) * math.sin(dlon/2)**2
    c = 2 * math.atan2(math.sqrt(a), math.sqrt(1 - a))
    distance = 6371 * c  # 지구의 평균 반지름은 약 6371km입니다.

    # 거리를 미터로 변환하여 반환합니다.
    return distance * 1000

####################################################################################

# 시작점과 목표점의 좌표
'''start = (37.3733, 126.6326) # 예: 인천대학교
end = (37.3785, 126.6326)
''' # 예: 인입역

start = (37.37463, 126.63481) 
end = (37.37521, 126.63390)

for index, row in df.iterrows():
    G.add_node(row['node_id'], x=row['longitude'], y=row['latitude'])


all_nodes = df['node_id'].unique()

for index, row in df_link.iterrows():
    if row['s_node_id'] in all_nodes and row['e_node_id'] in all_nodes:
        node1 = G.nodes[row['s_node_id']]
        node2 = G.nodes[row['e_node_id']]
        distance = calculate_distance(node1, node2)
        G.add_edge(row['s_node_id'], row['e_node_id'], key=row['link_id'], length=distance)
        G.add_edge(row['e_node_id'], row['s_node_id'], key=row['link_id'], length=distance)


############################################################################

# 가장 가까운 노드 찾기
start_node = ox.distance.nearest_nodes(G, start[1], start[0])
end_node = ox.distance.nearest_nodes(G, end[1], end[0])


#######################################################################################
# A* 알고리즘으로 최단 경로 찾기
route = nx.astar_path(G, start_node, end_node, weight='length')

buffer = 0.003  # 이 값을 조절하여 여유분을 설정할 수 있습니다.

north = max(start[0], end[0]) + buffer
south = min(start[0], end[0]) - buffer
east = max(start[1], end[1]) + buffer +0.002
west = min(start[1], end[1]) - buffer

# 경로를 지도 위에 그리기
fig, ax = ox.plot_graph_route(G, route, bbox=(north, south, east, west))
