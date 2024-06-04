package jpabook.jpashop.domain;

import jakarta.persistence.Embeddable;
import lombok.Getter;

@Embeddable
@Getter
public class Address {
    private String city;
    private String street;
    private String zipcode;

    protected Address() {
    }

    public Address(String zipcode, String street, String city) {
        this.zipcode = zipcode;
        this.street = street;
        this.city = city;
    }
}
