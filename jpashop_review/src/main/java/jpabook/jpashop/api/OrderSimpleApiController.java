package jpabook.jpashop.api;

import jpabook.jpashop.domain.Address;
import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.OrderStatus;
import jpabook.jpashop.repository.OrderRepository;
import jpabook.jpashop.repository.OrderSearch;
import jpabook.jpashop.repository.order.simplequery.OrderSimpleQueryDto;
import jpabook.jpashop.repository.order.simplequery.OrderSimpleQueryRepository;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

// xToOne
// Order
// Order -> Member
// Order -> Delivery
@RestController
@RequiredArgsConstructor
public class OrderSimpleApiController {
    private final OrderRepository orderRepository;
    private final OrderSimpleQueryRepository orderSimpleQueryRepository;

    //필요한 데이터만 노출시키는 것이 낫다. ==> DTO로 변환해서 반환하는 것이 더 좋은 방법
    @GetMapping("/api/v1/simple-orders")
    public List<Order> ordersV1() {
        List<Order> all = orderRepository.findAllByString(new OrderSearch());
        for (Order order : all) {
            //getMember() 까지는 빈 프록시 객체이지만
            //.getName()을 하면서 Lazy 강제 초기화가 된다
            order.getMember().getName(); //Lazy 강제 초기화
            order.getDelivery().getAddress(); //Lazy 강제 초기화
        }
        return all;
    }

    //V1과 V2 모두 3개의 테이블에 접근해야하기 때문에 쿼리문이 많이 나간다
    //ORDER -> SQL 1번 실행 -> 결과 주문 수 2개


    //N + 1 문제 발생
    @GetMapping("/api/v2/simple-orders")
    public List<SimpleOrderDto> orderV2() {
        //처음 ORDER 2개가 조회
        List<Order> orders = orderRepository.findAllByString(new OrderSearch());

        //loop를 돌 때
        //getName에서 Lazy 로딩 초기화 됨
        //getAddress에서 Lazy 로딩 초기화 됨
        //총 5번의 쿼리가 나가게 된다 ==> N + 1 문제

        //  주문 조회쿼리 1번  회원 2번 배송 2번 ==> 총 5번
        //N + 1 문제 == 1 + 회원 N + 배송 N
        // EAGER로 즉시 로딩을 하더라도 해결이 되지 않음
        // LAZY 로딩을 기본으로 하고 성능 최적화가 필요한 경우에는 fetch join을 사용하라
        List<SimpleOrderDto> collect = orders.stream()
                .map(o -> new SimpleOrderDto(o))
                .collect(Collectors.toList());
        return collect;


    }

    //fetch join 사용하여 쿼리가 한번만 나간다
    @GetMapping("/api/v3/simple-orders")
    public List<SimpleOrderDto> ordersV3() {
        List<Order> orders = orderRepository.findAllWithMemberDelivery();
        List<SimpleOrderDto> result = orders.stream()
                .map(o -> new SimpleOrderDto(o))
                .collect(Collectors.toList());
        return result;
    }

    //V4 버전은 select 절에서 원하는 데이터만 선택해서 조회하기 때문에 실제 실행되는 쿼리에서 select절에 들어가는 필드가 V3보다 적다
    //V3는 재사용할 수 있다
    //V4는 재사용성이 떨어진다. 완전 원하는 데이터만 선택하였기 때문이다, 성능 최적화에는 V4가 아주 약간 더 좋다.
    //V3 V4 둘다 장단점이 있어 상황에 맞춰 선택
    //
    // V4 방안을 사용할 시 추천하는 방법
    // API 스펙에 맞춘 코드는 성능 최적화된 쿼리용 패키지를 뽑아서 넣어놓는 것이 좋다.
    // 이렇게 함으로써 유지보수성이 좋아진다.

    // 1. 엔티티를 DTO로 변환하는 방법 선택
    // 2. 필요하면 fetch join으로 성능 최적화 --> 여기서 대부분의 성능 이슈 해결됨
    // 3. 그래도 안된다면 DTO로 직접 조회하는 방법
    // 4. JPA가 제공하는 네이티브 SQL이나 스프링 JDBC Template을 사용하여 SQL 직접 사용
    @GetMapping("/api/v4/simple-orders")
    public List<OrderSimpleQueryDto> orderV4() {
        return orderSimpleQueryRepository.findOrderDtos();
    }

    @Data
    static class SimpleOrderDto {
        private Long orderId;
        private String name;
        private LocalDateTime orderDate; //주문시간
        private OrderStatus orderStatus;
        private Address address;

        public SimpleOrderDto(Order order) {
            orderId = order.getId();
            name = order.getMember().getName();
            orderDate = order.getOrderDate();
            orderStatus = order.getStatus();
            address = order.getDelivery().getAddress();
        }
    }

}
