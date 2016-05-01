select calorie_id, meal, note, calorie_num, eat_time
from calories
where user_id=1 and not deleted and (0 = 0 or eat_time < to_timestamp(0)) order by eat_time desc limit (100);


select user_id, user_name, email, user_type, daily_calories
from users
where not deleted and ($1 is null or user_name > $1) order by user_name desc limit ($2)

select calorie_id, meal, note, calorie_num, eat_time
from calories
where user_id=1 and not deleted
    and eat_time between to_timestamp(1460408400000) and to_timestamp(1462827600000)
    and (0 = 0 or eat_time < to_timestamp(0)) order by eat_time desc limit (100)


    1460235600000
    1462827600000

    1462083783304
    1462127715320


select CAST(to_timestamp(1462083783304) AS TIME WITH TIME ZONE), CAST(to_timestamp(1462127715320) AS TIME WITH TIME ZONE);


select calorie_id, meal, note, calorie_num, eat_time from calories where user_id=1 and not deleted
    and eat_time between to_timestamp(1460235600000 / 1000) and to_timestamp(1462827600000 / 1000)
    and CAST(eat_time AS TIME WITH TIME ZONE) between CAST(to_timestamp(1462083783304 / 1000) AS TIME WITH TIME ZONE) and CAST(to_timestamp(1462127715320 / 1000) AS TIME WITH TIME ZONE)
and (0 = 0 or eat_time < to_timestamp(0)) order by eat_time desc limit (100);
