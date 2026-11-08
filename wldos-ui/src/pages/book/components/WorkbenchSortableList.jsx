import React, { useRef, useState } from 'react';
import classNames from 'classnames';
import styles from '../style.less';

const reorderItems = (list, fromIndex, toIndex) => {
  if (fromIndex === toIndex || fromIndex == null || toIndex == null) {
    return list;
  }
  const next = list.slice();
  const [moved] = next.splice(fromIndex, 1);
  next.splice(toIndex, 0, moved);
  return next;
};

const WorkbenchSortableList = ({
  items,
  selectedKey,
  onSelect,
  onReorder,
  renderSelectedExtra,
  onItemClickGuard,
}) => {
  const [dragIndex, setDragIndex] = useState(null);
  const [overIndex, setOverIndex] = useState(null);
  const dragIndexRef = useRef(null);
  const skipClickRef = useRef(false);

  const finishDrag = (fromIndex, toIndex) => {
    if (fromIndex == null || toIndex == null || fromIndex === toIndex) {
      return;
    }
    const moved = items[fromIndex];
    if (!moved) {
      return;
    }
    const reordered = reorderItems(items, fromIndex, toIndex);
    const orderedIds = reordered.map((row) => row.id);
    skipClickRef.current = true;
    window.setTimeout(() => {
      skipClickRef.current = false;
    }, 0);
    if (onReorder) {
      onReorder(orderedIds, {
        fromIndex,
        toIndex,
        movedId: moved.id,
      });
    }
  };

  if (!items?.length) {
    return null;
  }

  return (
    <ul className={styles.workbenchSortableList} role="menu">
      {items.map((item, index) => {
        const selected = String(selectedKey) === String(item.id);
        return (
          <li
            key={item.id}
            role="menuitem"
            draggable
            title={item.pubTitle}
            className={classNames(
              styles.workbenchSortableItem,
              selected && styles.workbenchSortableItemSelected,
              dragIndex === index && styles.workbenchSortableItemDragging,
              overIndex === index && dragIndex !== index && styles.workbenchSortableItemOver,
            )}
            onDragStart={(e) => {
              if (e.target?.closest?.(`.${styles.menuConfigTrigger}`)) {
                e.preventDefault();
                return;
              }
              dragIndexRef.current = index;
              setDragIndex(index);
              e.dataTransfer.effectAllowed = 'move';
              e.dataTransfer.setData('text/plain', String(item.id));
            }}
            onDragEnd={() => {
              dragIndexRef.current = null;
              setDragIndex(null);
              setOverIndex(null);
            }}
            onDragOver={(e) => {
              e.preventDefault();
              const from = dragIndexRef.current;
              if (from != null && from !== index) {
                setOverIndex((prev) => (prev === index ? prev : index));
              }
            }}
            onDragLeave={() => {
              setOverIndex((prev) => (prev === index ? null : prev));
            }}
            onDrop={(e) => {
              e.preventDefault();
              finishDrag(dragIndexRef.current, index);
              dragIndexRef.current = null;
              setDragIndex(null);
              setOverIndex(null);
            }}
            onClick={(e) => {
              if (skipClickRef.current) {
                return;
              }
              if (onItemClickGuard?.(e)) {
                return;
              }
              if (onSelect) {
                onSelect(item, e);
              }
            }}
          >
            <span className={styles.workbenchSortableTitle}>{item.pubTitle}</span>
            {selected && renderSelectedExtra ? (
              <div className={styles.workbenchSortableExtra}>{renderSelectedExtra(item)}</div>
            ) : null}
          </li>
        );
      })}
    </ul>
  );
};

export default WorkbenchSortableList;
