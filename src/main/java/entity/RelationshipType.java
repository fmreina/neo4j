package entity;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum RelationshipType {

	HAS_NODE("has_node", "HAS_NODE"),
	SIBLING_OF("sibling_of", "SIBLING_OF"),
	FRIEND_OF("friend_of", "FRIEND_OF"),
	KNOWS("knows", "KNOWS");

	private String name;
	private String label;

}
