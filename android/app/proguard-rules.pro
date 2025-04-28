# Keep your model classes
-keep class com.bilkom.model.** { *; }

# Keep your API interface
-keep interface com.bilkom.network.ApiService { *; }

# Keep Gson classes
-keep class com.google.gson.** { *; }

# OkHttp rules
-dontwarn okhttp3.**
-dontwarn okio.**