select calorie_id, meal, note, calorie_num, eat_time
from calories
where user_id=1 and not deleted and (0 = 0 or eat_time < to_timestamp(0)) order by eat_time desc limit (100);


select user_id, user_name, email, user_type, daily_calories
from users
where not deleted and ($1 is null or user_name > $1) order by user_name desc limit ($2)
