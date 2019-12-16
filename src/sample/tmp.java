package sample;

public class tmp {
    public static void main(String[] args) {
        int[] array = new int[] {7, 10 , -5,  123, 0, 168, 33, -1, 12, 21 , 22, 3, 1, 2, 45,  8, 4};

        for (int i = 0; i < array.length; i++) {
            System.out.print(array[i] + " ");
        }
        System.out.println();

        for (int i = array.length-2; i >= 0; i--) {
            int key = array[i];
            int j = i+1;
            while (j < array.length && array[j] < key) {
                array[j-1] = array[j];
                j++;
            }
            array[j-1] = key;
        }

        for (int i = 0; i < array.length; i++) {
            System.out.print(array[i] + " ");
        }
    }
}
