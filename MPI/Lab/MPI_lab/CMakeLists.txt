cmake_minimum_required(VERSION 3.16)
project(MPI_lab C)

set(CMAKE_C_STANDARD 11)

find_package(MPI REQUIRED)

if(NOT TARGET MPI::MPI_CXX)
    add_library(MPI::MPI_CXX IMPORTED INTERFACE)

    set_property(TARGET MPI::MPI_CXX
            PROPERTY INTERFACE_COMPILE_OPTIONS ${MPI_CXX_COMPILE_FLAGS})
    set_property(TARGET MPI::MPI_CXX
            PROPERTY INTERFACE_INCLUDE_DIRECTORIES "${MPI_CXX_INCLUDE_PATH}")
    set_property(TARGET MPI::MPI_CXX
            PROPERTY INTERFACE_LINK_LIBRARIES ${MPI_CXX_LINK_FLAGS} ${MPI_CXX_LIBRARIES})
endif()

add_executable(MPI_lab
        guess.c
        monte_carlo.c)

set_target_properties(MPI_lab PROPERTIES RUNTIME_OUTPUT_DIRECTORY "${CMAKE_BINARY_DIR}/Exec")
target_compile_options(MPI_lab PRIVATE ${MPI_CXX_COMPILE_FLAGS})
target_link_libraries(MPI_lab PUBLIC MPI::MPI_C)