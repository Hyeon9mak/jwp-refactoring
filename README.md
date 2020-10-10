# 키친포스

## 요구 사항

- 메뉴 그룹을 등록할 수 있다.
- 메뉴 그룹의 리스트를 조회할 수 있다.
- 메뉴를 추가할 수 있다.
    - 추가적으로 메뉴가 가지고 있는 메뉴 상품도 DB에 저장한다.
    - 예외 사항 
        - 메뉴의 가격이 비어있거나 0원 미만이면 예외를 발생한다.
        - 아무 메뉴 그룹에도 속하지 않을 시 예외를 발생한다.
        - 메뉴의 가격이 개별 상품 가격의 총합보다 비싸면 예외를 발생한다.
- 메뉴의 리스트를 조회할 수 있다.
- 주문을 등록할 수 있다.
    - 주문을 등록할 때 주문 상태는 '조리'로 저장한다.
    - 추가적으로 주문이 가지고 있는 주문 항목도 DB에 저장한다.
    - 예외 사항
        - 주문 항목이 없을 시 예외를 발생한다.
        - 주문 항목이 중복되거나 없는 주문 항목일 시 예외를 발생한다.
        - 주문 테이블이 없는 테이블일 시 예외를 발생한다.
        - 주문 테이블에 손님이 없을 시 예외를 발생한다.
- 주문 리스트를 조회할 수 있다.
- 주문의 상태를 변경할 수 있다.
    - 예외 사항
        - 주문의 상태가 '계산 완료'일 시 예외를 발생한다.
- 상품을 등록할 수 있다.
    - 예외 사항 
        - 상품의 가격이 비어있거나 0원 미만이면 예외를 발생한다.
- 상품의 리스트를 조회할 수 있다.
- 단체 지정을 할 수 있다.
    - 추가적으로 단체 지정된 주문 테이블들도 DB에 저장한다.
    - 예외 사항
        - 주문 테이블이 비어있거나 1개일 시 예외를 발생한다.
        - 주문 테이블이 중복되거나 없는 주문 테이블일 시 예외를 발생한다.
        - 각 주문 테이블에 사람이 없거나 단체지정이 안 되어 있을 시 예외를 발생한다.
- 단체 지정을 해제할 수 있다.
    - 예외 사항
        - 주문 테이블 중 주문이 안 들어가거나 계산 완료되지 않은 테이블이 있을 시 예외를 발생한다.
- 주문 테이블을 추가할 수 있다.
- 주문 테이블의 리스트를 조회할 수 있다.
- 주문 테이블을 비워주거나 채워줄 수 있다. (개별 주문 테이블일 경우)
    - 예외 사항
        - 주문 테이블이 없는 경우 예외를 발생한다.
        - 단체 지정 되어 있을 경우 예외를 발생한다.
        - 주문 테이블들 중 주문이 안 들어가거나 계산 완료되지 않은 테이블이 있을 시 예외를 발생한다.
- 주문 테이블의 방문한 손님의 수를 변경해 줄 수 있다.
    - 예외 사항
        - 방문한 손님의 수가 0 미만일 경우 예외를 발생한다.
        - 주문 테이블이 없는 경우 예외를 발생한다.
        - 주문 테이블이 비어있는 경우 예외를 발생한다.


## 용어 사전

| 한글명 | 영문명 | 설명 |
| --- | --- | --- |
| 상품 | product | 메뉴를 관리하는 기준이 되는 데이터 |
| 메뉴 그룹 | menu group | 메뉴 묶음, 분류 |
| 메뉴 | menu | 메뉴 그룹에 속하는 실제 주문 가능 단위 |
| 메뉴 상품 | menu product | 메뉴에 속하는 수량이 있는 상품 |
| 금액 | amount | 가격 * 수량 |
| 주문 테이블 | order table | 매장에서 주문이 발생하는 영역 |
| 빈 테이블 | empty table | 주문을 등록할 수 없는 주문 테이블 |
| 주문 | order | 매장에서 발생하는 주문 |
| 주문 상태 | order status | 주문은 조리 ➜ 식사 ➜ 계산 완료 순서로 진행된다. |
| 방문한 손님 수 | number of guests | 필수 사항은 아니며 주문은 0명으로 등록할 수 있다. |
| 단체 지정 | table group | 통합 계산을 위해 개별 주문 테이블을 그룹화하는 기능 |
| 주문 항목 | order line item | 주문에 속하는 수량이 있는 메뉴 |
| 매장 식사 | eat in | 포장하지 않고 매장에서 식사하는 것 |
