# For more information about using CMake with Android Studio, read the
# documentation: https://d.android.com/studio/projects/add-native-code.html.
# For more examples on how to use CMake, see https://github.com/android/ndk-samples.

# Sets the minimum CMake version required for this project.
cmake_minimum_required(VERSION 3.22.1)


# Build the Oboe library
set (OBOE_DIR ../../../../oboe)
add_subdirectory (${OBOE_DIR} ./oboe)

# Make the Oboe public headers available to our app
include_directories (${OBOE_DIR}/include)





# include folders
include_directories(
        ${CMAKE_CURRENT_LIST_DIR}
)

set(APP_SOURCES
        SimpleToneMaker.cpp
        OboeAudioJNI.cpp
        )

add_library(simpletone SHARED
        ${APP_SOURCES}
        #Oscillator.cpp
        )





# Specify the libraries which our native library is dependent on, including Oboe
target_link_libraries (simpletone log oboe)