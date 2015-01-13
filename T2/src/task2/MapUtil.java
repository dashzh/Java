package task2;

import java.text.MessageFormat;
import java.util.Map;
import java.util.Map.Entry;

public class MapUtil {
	
	
	//Преобразует распределитель в его строковое представление вида {{key : x, value : y}, {key : x, value : y}, ...}
	// parametrs m искомый распределитель
	// return строковое представление распределителя
	public static String toString(Map<? extends Object, ? extends Object> m) {
		StringBuffer sb = new StringBuffer();
		if (m != null) {
			sb.append("{");
			int i = 1;
			for (Entry<? extends Object, ? extends Object> entry : m.entrySet()) {
				Object key = entry.getKey();
				Object value = entry.getValue();
				sb.append(
					MessageFormat.format(
						"'{'key : {0}, value : {1}'}'{2}",
						key, value, (i < m.size()) ? ", " : ""
					)
				);
				i++;
			}
			sb.append("}");
		} else
			sb.append("null");
		
		return sb.toString();
	}
}
