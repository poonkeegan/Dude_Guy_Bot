package com.keegan.Dude_Guy_Bot;

public class StringQueue {



  private class StringNode {
    private String value;
    private StringNode next_node;

    public StringNode(String v, StringNode n){
      value = v;
      next_node = n;
    }

    public StringNode(String v){
      value = v;
      next_node = null;
    }

    public String get_val(){
      return value;
    }

    public StringNode get_next(){
      return next_node;
    }

    public void set_val(String v){
      value = v;
    }

    public void set_next(StringNode n){
      next_node = n;
    }
  }

}
