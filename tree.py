import os
import sys
# python tree.py . 3 > wldos_structure.txt
def tree(directory, prefix="", max_depth=3, current_depth=0, exclude_dirs=None):
    if exclude_dirs is None:
        exclude_dirs = ['node_modules', '.git', 'target', 'dist']

    if current_depth >= max_depth:
        return

    try:
        items = sorted([item for item in os.listdir(directory)
                       if not any(exclude in item for exclude in exclude_dirs)])

        for i, item in enumerate(items):
            path = os.path.join(directory, item)
            is_last = i == len(items) - 1
            current_prefix = "└── " if is_last else "├── "
            print(prefix + current_prefix + item)

            if os.path.isdir(path):
                next_prefix = prefix + ("    " if is_last else "│   ")
                tree(path, next_prefix, max_depth, current_depth + 1, exclude_dirs)
    except PermissionError:
        pass

if __name__ == "__main__":
    root = sys.argv[1] if len(sys.argv) > 1 else "."
    max_depth = int(sys.argv[2]) if len(sys.argv) > 2 else 3
    tree(root, max_depth=max_depth)