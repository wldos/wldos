/** 与 {@code PubTypeEnum.isComplex} 对齐：主作品下挂 chapter 的复合类型 */
const CHAPTER_CONTAINER_PUB_TYPES = new Set([
  'doc',
  'book',
  'annual',
  'class',
  'cook',
  'music',
  'medical',
  'genealogy',
  'game',
  'sports',
  'theatre',
  'paint',
  'poetry',
  'dance',
]);

export function isChapterContainerPubType(pubType) {
  if (!pubType) {
    return false;
  }
  return CHAPTER_CONTAINER_PUB_TYPES.has(String(pubType).toLowerCase());
}
