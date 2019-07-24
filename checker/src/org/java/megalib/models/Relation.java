package org.java.megalib.models;

public class Relation {
	private String subject;
	private String object;
	private Block block;

	public Relation(String s, String o){
		subject = s;
		object = o;
	}

	public String getSubject(){
		return subject;
	}

	public String getObject(){
		return object;
	}

	public Block getBlock(){
		return block;
	}

	public void setBlock(Block b){
		block = b;
	}

	public String getModule(){
		return block.getModule();
	}

	@Override
    public String toString() {
        return subject + " R " + object;
    }

    @Override
	public boolean equals(Object obj) {
		if ( this == obj )
		      return true;
		if ( obj == null )
		      return false;
		if ( getClass() != obj.getClass() )
		      return false;
		return subject.equals(((Relation) obj).getSubject())
				&& object.equals(((Relation) obj).getObject());
	}

	@Override
	public int hashCode(){
		return (subject+object).hashCode();
	}
}
