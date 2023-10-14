package de.ksbrwsk.people;

import org.springframework.boot.SpringApplication;

class MvcPeopleApplicationTests {
	public static void main(String[] args) {
		SpringApplication.from(MvcPeopleApplication::main)
				.with(PostgresTestContainer.class)
				.run(args);
	}

}
