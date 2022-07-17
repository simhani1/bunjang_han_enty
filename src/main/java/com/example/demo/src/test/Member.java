package com.example.demo.src.test;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class Member {
    private List<String> hobby;
    private SecondMember secondMember;
}
