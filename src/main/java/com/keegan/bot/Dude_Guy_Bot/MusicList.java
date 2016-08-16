package com.keegan.Dude_Guy_Bot;

public class MusicList {
  private String[][] contents;

  public MusicList(){
    // Create a 10 long array for Song titles and Song locations
    contents = new String[10][2];
  }

  public void push(String[] song_vals){
    /**
    * Adds a new entry into the top of the List
    */
    // Check the array has 2 elements to insert into the list
    if (song_vals.length > 1) {
      push(song_vals[0], song_vals[1]);
    }
  }

  public void push(String song_title, String song_loc){
    /**
    * Adds a new entry into the top of the list
    */
    cycle_list();
    contents[0][0] = song_title;
    contents[0][1] = song_loc;
  }

  public String toString(){
    /**
    * Convert the list into a human readable ordered list of
    * song titles, or URLs if titles are not available
    */
    int curr_len = length();
    String list = "";
    // Go through every song in the list
    for(int i = 0; i < curr_len; i++){
      // Find out if there is a song title to print
      String music_name;
      if (contents[i][0] != null){
        music_name = contents[i][0];
      }else{
        music_name = contents[i][1];
      }
      // Add song to the string
      list += i + ". " + music_name + "%n";
    }
    return list;
  }

  private void length(){
    /**
    * Returns the amount of non null entries in the list
    */
    // Figure out where the end of the list is
    int index = contents.length;
    // Check if current index is null
    boolean is_null = contents[index - 1][0] == null;
    is_null = is_null && contents[index - 1][1] == null;
    // If not loop until it is
    while (is_null && (index > 0)){
      index--;
      is_null = contents[index - 1][0] == null;
      is_null = is_null && contents[index - 1][1] == null;
    }
    return index;
  }

  private void cycle_list(){
    /**
    * Moves entries in the list one position down
    */
    // Figure out where the end of the list is
    // If the list is empty, nothing will get moved
    int curr_len = length();
    if (curr_len != 0){
      // Move down every entry
      for (int i = curr_len; i > 0; i--){
        // Don't try to move the last entry outside the array
        if (i != contents.length){
          move_down(i-1);
        }
      }
    }


  }

  private void move_down(int pos){
    /**
    * Copy the entry at pos one position down
    * REQ: pos within [0, contents.length - 1]
    */
    contents[pos + 1] = contents[pos];
  }


  public String[] get(){
    return get(0);
  }

  public String[] get(int pos){
    return contents[pos];
  }
  public String get_name(){
    return get_name(0);
  }
  public String get_name(int pos){
    String ret_name;
    if (contents[pos][0] != null){
      ret_name = contents[pos][0];
    }else{
      ret_name = contents[pos][1];
    }
    return ret_name;
  }
}
