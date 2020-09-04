package com.cy.page;

import cn.hutool.core.convert.Convert;
import org.junit.Test;

import java.text.NumberFormat;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;


public class CommonUtil {


	/**
	 * 快速List
	 *
	 * @param ts
	 * @param <T>
	 * @return
	 */
	public static <T> List<T> fastList(T... ts) {
		return Stream.of(ts).collect(Collectors.toList());
	}


	public static <T, V> Map<T, V> lineToMap(Collection<T> collection1, Collection<V> collection2) {
		Map<T, V> map = new HashMap<>();
		return null;
	}

	/**
	 * 合并
	 *
	 * @param list
	 * @param <T>
	 * @return
	 */
	public static <T> Collection<T> allForOne(Collection<Collection<T>> list) {
		return list.stream().flatMap(x -> {
			return x.stream();
		}).collect(Collectors.toList());
	}

	public static void main(String args[]) {
		Thread t = new Thread() {
			public void run() {
				pong();
			}
		};
		t.start();
		System.out.print("ping");
	}

	static void pong() {
		System.out.print("pong");
	}
	@Test
	public void nextTest(){
		int[] temp = {48,32,24,16};
		int value = 32;
		long count = Arrays.stream(temp).filter(x -> value >x||value==x).count();
		if(count<1){
			System.out.println("最小数太大");
		}else {
			OptionalInt min = Arrays.stream(temp).filter(x -> value > x).map(x ->value-x).min();
			System.out.println(value-min.getAsInt());
		}

	}

	@Test
	public void  numberBlack(){
		String format = String.format("%10s你觉得如何","\b");
		System.out.println(format);
	}

}
