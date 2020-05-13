package com.example.jdk_18.lambda_exercise;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.stream.Collectors;

/**
 * @author vincent
 */
public class LambdaTest {
    @Data
    private static class Student {
        private Integer id;
        private String name;
        private Integer age;
        private Integer interestType;
        private List<Hobby> hobbies;
        private List<Fancy> fancies;
        private Subject subject;

        private Student(Integer id, String name, Integer age, Integer interestType, List<Hobby> hobbies, List<Fancy> fancies, Subject subject) {
            this.id = id;
            this.name = name;
            this.age = age;
            this.interestType = interestType;
            this.hobbies = hobbies;
            this.fancies = fancies;
            this.subject = subject;
        }

    }

    enum InterestType {
        HOBBY(1, "兴趣"),
        FANCY(2, "喜好");
        private Integer code;
        private String message;

        InterestType(Integer code, String message) {
            this.code = code;
            this.message = message;
        }

        public Integer getCode() {
            return code;
        }

        public static InterestType getInterestTypeByCode(int code) {
            return Arrays.stream(InterestType.values()).filter(interestType -> interestType.getCode() == code).findFirst().orElse(null);
        }

    }

    @Data
    private static class Hobby {
        private String basketball;
        private String running;
        private String drinking;

        private Hobby(String basketball, String running, String drinking) {
            this.basketball = basketball;
            this.running = running;
            this.drinking = drinking;
        }
    }

    @Data
    private static class Fancy {
        private String dance;
        private String takePhotos;
        private String meetGirls;

        private Fancy(String dance, String takePhotos, String meetGirls) {
            this.dance = dance;
            this.takePhotos = takePhotos;
            this.meetGirls = meetGirls;
        }
    }

    @Data
    private static class Subject {
        private String english;
        private String chinese;
        private String mathematics;

        private Subject(String english, String chinese, String mathematics) {
            this.english = english;
            this.chinese = chinese;
            this.mathematics = mathematics;
        }
    }

    private List<Student> getStudent() {
        List<Student> list = Lists.newArrayList();
        list.add(new Student(100, "小明", 10, 1,
                Lists.newArrayList(
                        new Hobby("篮球", "跑步", "喝酒"),
                        new Hobby("篮球_1", "跑步_1", "喝酒_1"),
                        new Hobby("篮球_2", "跑步_2", "喝酒_2"),
                        new Hobby("篮球_3", "跑步_3", "喝酒_3")
                ),
                Lists.newArrayList(
                        new Fancy("街舞", "摄影", "泡妞"),
                        new Fancy("街舞_1", "摄影_1", "泡妞_1"),
                        new Fancy("街舞_2", "摄影_2", "泡妞_2"),
                        new Fancy("街舞_3", "摄影_3", "泡妞_3")
                ),
                new Subject("英语", "语文", "数学")
        ));
        list.add(new Student(200, "小红", 10, 2,
                Lists.newArrayList(
                        new Hobby("篮球", "跑步", "喝酒"),
                        new Hobby("篮球_1", "跑步_1", "喝酒_1"),
                        new Hobby("篮球_2", "跑步_2", "喝酒_2"),
                        new Hobby("篮球_3", "跑步_3", "喝酒_3")
                ),
                Lists.newArrayList(
                        new Fancy("街舞", "摄影", "泡妞"),
                        new Fancy("街舞_1", "摄影_1", "泡妞_1"),
                        new Fancy("街舞_2", "摄影_2", "泡妞_2"),
                        new Fancy("街舞_3", "摄影_3", "泡妞_3")
                ),
                new Subject("英语", "语文", "数学")
        ));
        return list;
    }

    @Data
    private static class Person {
        private Integer pid;
        private String pname;
        private Integer page;
        private String interest;
        private String subject;
    }


    private final static BiConsumer<Person, Student> HOBBY = (person, student) -> {
        Optional.ofNullable(student.getHobbies())
                .flatMap(hobbies -> hobbies.stream().findFirst())
                .ifPresent(hobby -> person.setInterest(hobby.getDrinking()));
        Optional.ofNullable(student.subject).ifPresent(subject -> person.setSubject(subject.getEnglish()));
    };

    private final static BiConsumer<Person, Student> FANCY = (person, student) -> {
        Optional.ofNullable(student.getFancies())
                .flatMap(fancies -> fancies.stream().findFirst())
                .ifPresent(fancy -> person.setInterest(fancy.getDance()));
        Optional.ofNullable(student.subject).ifPresent(subject -> person.setSubject(subject.getMathematics()));
    };

    private final static ImmutableMap<InterestType, BiConsumer<Person, Student>> OF = ImmutableMap.of(
            InterestType.HOBBY, HOBBY,
            InterestType.FANCY, FANCY
    );

    /**
     * BiConsumer<T, U> 实例
     */
    @Test
    public void t() {
        List<Student> studentList = getStudent();
        List<Object> collect = studentList.stream().map(student -> {
            Person person = new Person();
            Optional.ofNullable(student).ifPresent(stu -> {
                person.setPid(stu.getId());
                person.setPname(stu.getName());
                person.setPage(stu.getAge());

                Integer interestType = student.getInterestType();
                InterestType interestTypeByCode = InterestType.getInterestTypeByCode(interestType);
                person.setInterest(interestTypeByCode.message);

                OF.get(interestTypeByCode).accept(person, student);
            });
            return person;
        }).collect(Collectors.toList());

        System.out.println(collect);
    }


    @Data
    private static class trip {
        private String departure;
        private String destination;

        private trip(String departure, String destination) {
            this.departure = departure;
            this.destination = destination;
        }
    }

    private static final BinaryOperator<String> ACCUMULATOR = (v1, v2) -> {
        if (StringUtils.isEmpty(v1)) {
            return v2;
        }
        String[] item = StringUtils.split(v1, ",");
        String[] lastOfItem = StringUtils.split(item[item.length - 1], "-");
        String lastElement = lastOfItem[lastOfItem.length - 1];
        String[] nextItem = StringUtils.split(v2, "-");
        String startElement = nextItem[0];
        if (StringUtils.equals(lastElement, startElement)) {
            return v1 + "-" + nextItem[nextItem.length - 1];
        }
        return v1 + "," + v2;
    };

    @Test
    public void t2() {
        List<trip> list = Lists.newArrayList(
                new trip("上海", "北京"),
                new trip("北京", "上海"),
                new trip("天津", "西安"),
                new trip("拉萨", "灵芝"),
                new trip("灵芝", "兰州"),
                new trip("兰州", "西宁")
        );

        //[上海-北京-上海,天津-西安,拉萨-灵芝-兰州-西宁]
        String reduce = list.stream()
                .map(t -> String.format("%s-%s", t.getDeparture(), t.getDestination()))
                .reduce("", ACCUMULATOR);
        System.out.println(reduce);
    }


}














