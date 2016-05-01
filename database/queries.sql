select calorie_id, meal, note, calorie_num, eat_time
from calories
where user_id=1 and not deleted and (0 = 0 or eat_time < to_timestamp(0)) order by eat_time desc limit (100);
