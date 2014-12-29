package ebs.music;

/**
 * Created by Aleksey Dubov
 * Date: 14/09/13
 * Time: 22:51
 * Copyright (c) 2014
 */
public interface Function<F, T> {
	T apply(F from);
}
