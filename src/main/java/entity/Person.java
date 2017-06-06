package entity;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class Person {

	private String name = "";
	private String surname = "";
	private int age = 0;
	private double height = 0.0;
	private double weight = 0.0;

}
