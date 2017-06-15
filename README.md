## Intro
Android course project 1.

## Configuration

You need to create strings.xml from example file.

```
cd app/src/main/res/values

cp strings_example.xml strings.xml
```

Then uncomment, and change the moviedb key to actual key value.

## Dev Note
to pull down the sqlite db on emulator. run
```
adb -e shell
su
cp /data/data/com.example.dorren.popmovies/databases/movies.db /sdcard
```
then exit shell, and pull it down in console.
```
adb -e pull /sdcard/movies.db .
```

tried other ways like run-as etc, not working.
