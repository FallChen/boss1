package com.cy.page;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class CommonUtil {


	/**
	 * 快速List
	 * @param ts
	 * @param <T>
	 * @return
	 */
	public  static <T>  List<T> fastList(T... ts){
		return Stream.of(ts).collect(Collectors.toList());
	}

	public static <T,V> Map<T,V> lineToMap(Collection<T> collection1,Collection<V> collection2){
		Map<T,V> map = new HashMap<>();
		return null;
	}

	/**
	 * 合并
	 * @param list
	 * @param <T>
	 * @return
	 */
	public static <T> Collection<T> allForOne(Collection<Collection<T>> list){
		return list.stream().flatMap(x -> { return x.stream(); }).collect(Collectors.toList());
	}


}
