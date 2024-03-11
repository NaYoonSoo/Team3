# 코드재료들 - 파일 실행 x 
#-------------------------------------------------------------------------------------------------------------------------------------------------------------------------
# 장애물의 정보
obstacle_center = (37.375, 126.635)  # 장애물의 중심 좌표
obstacle_size = 0.001  # 장애물의 크기

# 장애물의 영역
obstacle_north = obstacle_center[0] + obstacle_size
obstacle_south = obstacle_center[0] - obstacle_size
obstacle_east = obstacle_center[1] + obstacle_size
obstacle_west = obstacle_center[1] - obstacle_size

# 장애물이 포함하는 영역 내의 모든 노드 찾기
obstacle_nodes = [node for node, data in G.nodes(data=True) if obstacle_south <= data['y'] <= obstacle_north and obstacle_west <= data['x'] <= obstacle_east]

# 장애물이 포함하는 영역 내의 모든 엣지의 비용을 높게 설정
for node in obstacle_nodes:
    for neighbor in G.neighbors(node):
        if neighbor in obstacle_nodes:
            G[node][neighbor][0]['length'] = 999999


#-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
# 새로운 노드 추가
G.add_node('new_node', x=126.6356, y=37.3753)

# 새로운 엣지 추가
G.add_edge('new_node', start_node, length=100)  # length는 엣지의 가중치를 나타냅니다.

