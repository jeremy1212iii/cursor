#!/usr/bin/env python3

def draw_trump():
    try:
        with open('trump.txt', 'r') as f:
            print(f.read())
    except FileNotFoundError:
        print("Error: trump.txt not found.")
        # Fallback art
        print("ASCII art missing. Imagine a very famous hairstyle.")

if __name__ == "__main__":
    draw_trump()
