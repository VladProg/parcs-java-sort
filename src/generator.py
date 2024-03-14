import sys
import random

n = int(sys.argv[1])
print(n)
for _ in range(n):
    print(random.randint(1, 10 ** 9))
