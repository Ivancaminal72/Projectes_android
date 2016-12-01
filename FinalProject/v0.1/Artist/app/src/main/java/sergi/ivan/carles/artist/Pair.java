package sergi.ivan.carles.artist;

/**
 * Created by Ivan on 01/12/2016.
 */

public class Pair<String, Integer> {

        private String first;//first member of pair
        private Integer second;//second member of pair

        public Pair(String first, Integer second){
            this.first = first;
            this.second = second;
        }

        public void setFirst(String first){
            this.first = first;
        }

        public void setSecond(Integer second) {
            this.second = second;
        }

        public String getFirst() {
            return first;
        }

        public Integer getSecond() {
            return second;
        }


}
