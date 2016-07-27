package fr.pokegoboost.wrapper;

import lombok.Builder;
import lombok.Data;

@Data @Builder
public class ItemWrapper {
	
	private int id;
	private int count;
}
