import React, {useEffect, useRef, useState, useCallback, useMemo} from 'react';
import {Form, Input, message, Alert, Space, Typography, Button, Tabs, Modal} from "antd";
import {SaveOutlined, CheckCircleOutlined, ClockCircleOutlined, EditOutlined, EyeOutlined, BoldOutlined, ItalicOutlined, LinkOutlined, PictureOutlined, UnorderedListOutlined, CodeOutlined, UndoOutlined, RedoOutlined, EyeOutlined as PreviewOutlined, QuestionCircleOutlined, SunOutlined, MoonOutlined, FullscreenOutlined, FullscreenExitOutlined} from '@ant-design/icons';
import './BaseView.less';
import {Editor} from "@tinymce/tinymce-react";
import {saveChapter, uploadFile} from "@/pages/book/service";
import config from "@/utils/config";
import ReactMarkdown from 'react-markdown';
import remarkGfm from 'remark-gfm';
import rehypeRaw from 'rehype-raw';
import { Prism as SyntaxHighlighter } from 'react-syntax-highlighter';
import { vscDarkPlus, vs } from 'react-syntax-highlighter/dist/esm/styles/prism';
import { renderToString } from 'react-dom/server';
import { EditorView } from '@codemirror/view';
import { EditorState } from '@codemirror/state';
import { markdown } from '@codemirror/lang-markdown';
import { oneDark } from '@codemirror/theme-one-dark';
import { syntaxHighlighting, HighlightStyle } from '@codemirror/language';
import { tags } from '@lezer/highlight';
import MarkdownImage from '@/components/MarkdownImage';

export const {prefix} = config;

// 白色主题语法高亮样式
const lightHighlightStyle = HighlightStyle.define([
  { tag: tags.heading, color: "#0366d6", fontWeight: "bold" },
  { tag: tags.strong, color: "#24292e", fontWeight: "bold" },
  { tag: tags.emphasis, color: "#24292e", fontStyle: "italic" },
  { tag: tags.link, color: "#0366d6" },
  { tag: tags.quote, color: "#6a737d", fontStyle: "italic" },
  { tag: tags.monospace, backgroundColor: "#f6f8fa", color: "#24292e" },
  { tag: tags.list, color: "#24292e" },
  { tag: tags.keyword, color: "#d73a49", fontWeight: "bold" },
  { tag: tags.string, color: "#032f62" },
  { tag: tags.comment, color: "#6a737d", fontStyle: "italic" },
  { tag: tags.number, color: "#005cc5" },
  { tag: tags.operator, color: "#d73a49" },
  { tag: tags.punctuation, color: "#24292e" },
  { tag: tags.bracket, color: "#24292e" },
  { tag: tags.variableName, color: "#6f42c1" },
  { tag: tags.function, color: "#6f42c1" },
  { tag: tags.className, color: "#6f42c1" },
  { tag: tags.typeName, color: "#6f42c1" }
]);

// 暗黑主题语法高亮样式
const darkHighlightStyle = HighlightStyle.define([
  { tag: tags.heading, color: "#79c0ff", fontWeight: "bold" },
  { tag: tags.strong, color: "#f0f6fc", fontWeight: "bold" },
  { tag: tags.emphasis, color: "#f0f6fc", fontStyle: "italic" },
  { tag: tags.link, color: "#79c0ff" },
  { tag: tags.quote, color: "#8b949e", fontStyle: "italic" },
  { tag: tags.monospace, backgroundColor: "#21262d", color: "#f0f6fc" },
  { tag: tags.list, color: "#f0f6fc" },
  { tag: tags.keyword, color: "#ff7b72", fontWeight: "bold" },
  { tag: tags.string, color: "#a5d6ff" },
  { tag: tags.comment, color: "#8b949e", fontStyle: "italic" },
  { tag: tags.number, color: "#79c0ff" },
  { tag: tags.operator, color: "#ff7b72" },
  { tag: tags.punctuation, color: "#f0f6fc" },
  { tag: tags.bracket, color: "#f0f6fc" },
  { tag: tags.variableName, color: "#d2a8ff" },
  { tag: tags.function, color: "#d2a8ff" },
  { tag: tags.className, color: "#d2a8ff" },
  { tag: tags.typeName, color: "#d2a8ff" }
]);


const handleSave = async (fields, chapter = {pubTitle: '', pubContent: '',}) => {

  try {
    if (!fields || !Object.keys(fields).length)
      return false;
    if (chapter.pubContent === fields.pubContent && chapter.pubTitle === fields.pubTitle) {
      return false;
    }
    const res = await saveChapter({
      ...fields,
    });
    if (res?.data !== 'ok') {
      message.error(res.data.error?? '保存失败请重试');

      return false;
    }
    return true;
  } catch (error) {
    message.error('保存失败请重试！');
    return false;
  }
};

export const imgUploadHandler = async (img, success, failure, form) => {
  if (img.blob()) {
    const isGt5M = img.blob.size / 1024 / 1024 > 15;
    if (isGt5M) {
      message.warn('图片尺寸超限, 请压缩后上传');
      return;
    }
    const formData = new FormData();
    formData.append("file", img.blob(), img.filename());
    formData.append("id", form.getFieldValue("id"));
    const res = await uploadFile(formData);
    if (res?.data)
      success(res.data.url);
    else
      failure("上传失败");
  }
};

export const filePickerCallBack = async (callback, value, meta, form) => {
  // 支持的类型有：media、image、file
  if (meta.filetype === 'media') { // 媒体类型
    const input = document.createElement('input');
    input.setAttribute('type', 'file');
    input.onchange = async function() {
      const file = this.files[0];
      const formData = new FormData();
      formData.append("file", file);
      formData.append("id", form.getFieldValue("id"));
      const res = await uploadFile(formData);
      if (res?.data)
        callback(res.data.url);
    }
    input.click();
  }
};

const FormItem = Form.Item;

const {Text} = Typography;

const BookView = ({dispatch, currentChapter={id: '', parentId: '', pubTitle: '', pubContent: ''}, isSingle=false }) => {

  const [form] = Form.useForm();
  
  // 将form和editorView暴露到全局，供脚本使用
  useEffect(() => {
    window.currentForm = form;
    window.currentEditorView = editorView;
    return () => {
      window.currentForm = null;
      window.currentEditorView = null;
    };
  }, [form, editorView]);
  const timerId = useRef(null);
  const dirtyRef = useRef(false); // 是否有未提交的改动
  const FOREGROUND_DELAY = 1000; // 前台节流
  const BACKGROUND_DELAY = 4000; // 后台节流
  const [debounceDelay, setDebounceDelay] = useState(FOREGROUND_DELAY);
  const editorRef = useRef(null);
  const [data, setData] = useState(currentChapter?.pubContent?? '');
  const [saveStatus, setSaveStatus] = useState('idle'); // idle, saving, saved, error
  const [lastSaveTime, setLastSaveTime] = useState(null);
  
  // 编辑器模式状态
  const [editorMode, setEditorMode] = useState('rich'); // 'rich' | 'markdown'
  const [markdownContent, setMarkdownContent] = useState('');
  const [lastMarkdownContent, setLastMarkdownContent] = useState(''); // 保存最后一次的Markdown内容
  const [isContentModified, setIsContentModified] = useState(false); // 标记内容是否被修改
  const [isNewContent, setIsNewContent] = useState(false); // 是否为新建内容
  const [modeLocked, setModeLocked] = useState(false); // 模式是否已锁定
  const [markdownHistory, setMarkdownHistory] = useState([]); // Markdown历史记录
  const [historyIndex, setHistoryIndex] = useState(-1); // 当前历史索引
  const [isDarkTheme, setIsDarkTheme] = useState(false); // 主题状态：true为深色，false为浅色
  const [editorView, setEditorView] = useState(null); // CodeMirror编辑器实例
  const [isFullscreen, setIsFullscreen] = useState(false); // 全屏状态

  // 主题切换函数
  const toggleTheme = useCallback(() => {
    setIsDarkTheme(prev => !prev);
  }, []);

  // 全屏切换函数
  const toggleFullscreen = useCallback(() => {
    setIsFullscreen(prev => !prev);
  }, []);

  // 监听ESC键退出全屏
  useEffect(() => {
    const handleKeyDown = (event) => {
      if (event.key === 'Escape' && isFullscreen) {
        setIsFullscreen(false);
      }
    };

    if (isFullscreen) {
      document.addEventListener('keydown', handleKeyDown);
      return () => {
        document.removeEventListener('keydown', handleKeyDown);
      };
    }
  }, [isFullscreen]);

  // 创建CodeMirror编辑器
  const createCodeMirrorEditor = useCallback((container, initialValue) => {
    if (editorView) {
      editorView.destroy();
    }

    const extensions = [
      markdown(),
      EditorView.lineWrapping,
      EditorView.theme({
        "&": {
          fontSize: "16px",
          fontFamily: '"JetBrains Mono", "Fira Code", "Cascadia Code", "SF Mono", Monaco, "Consolas", "Liberation Mono", "Courier New", monospace',
          fontWeight: "600",
        },
        ".cm-content": {
          padding: "20px",
          lineHeight: "1.6",
          fontSize: "16px",
          fontWeight: "600",
        },
        ".cm-focused": {
          outline: "none",
        },
        ".cm-editor": {
          height: "100%",
          minHeight: "100%",
          flex: 1,
        },
        ".cm-scroller": {
          fontFamily: '"JetBrains Mono", "Fira Code", "Cascadia Code", "SF Mono", Monaco, "Consolas", "Liberation Mono", "Courier New", monospace',
          fontSize: "16px",
          fontWeight: "600",
          overflow: 'auto'
        },
        // Markdown 语法高亮
        ".cm-header": {
          color: isDarkTheme ? "#79c0ff" : "#0366d6",
          fontWeight: "bold"
        },
        ".cm-strong": {
          color: isDarkTheme ? "#f0f6fc" : "#24292e",
          fontWeight: "bold"
        },
        ".cm-emphasis": {
          color: isDarkTheme ? "#f0f6fc" : "#24292e",
          fontStyle: "italic"
        },
        ".cm-link": {
          color: isDarkTheme ? "#79c0ff" : "#0366d6",
          textDecoration: "underline"
        },
        ".cm-quote": {
          color: isDarkTheme ? "#8b949e" : "#6a737d",
          fontStyle: "italic"
        },
        ".cm-monospace": {
          backgroundColor: isDarkTheme ? "#21262d" : "#f6f8fa",
          color: isDarkTheme ? "#f0f6fc" : "#24292e",
          padding: "2px 4px",
          borderRadius: "3px"
        },
        ".cm-list": {
          color: isDarkTheme ? "#f0f6fc" : "#24292e"
        },
        ".cm-strikethrough": {
          textDecoration: "line-through",
          color: isDarkTheme ? "#8b949e" : "#6a737d"
        }
      }),
      EditorView.updateListener.of((update) => {
        if (update.docChanged) {
          const newValue = update.state.doc.toString();
          handleMarkdownChange(newValue);
        }
      })
    ];

    // 根据主题添加样式
    if (isDarkTheme) {
      extensions.push(oneDark);
    } else {
      // 白色主题的语法高亮
      extensions.push(EditorView.theme({
        "&": {
          color: "#24292e",
          backgroundColor: "#ffffff"
        },
        ".cm-content": {
          color: "#24292e"
        },
        ".cm-header": {
          color: "#0366d6",
          fontWeight: "bold"
        },
        ".cm-strong": {
          color: "#24292e",
          fontWeight: "bold"
        },
        ".cm-em": {
          color: "#24292e",
          fontStyle: "italic"
        },
        ".cm-link": {
          color: "#0366d6"
        },
        ".cm-quote": {
          color: "#6a737d",
          fontStyle: "italic"
        },
        ".cm-code": {
          backgroundColor: "#f6f8fa",
          color: "#24292e"
        },
        ".cm-list": {
          color: "#24292e"
        }
      }));
    }

    // 添加高度主题
    const heightTheme = EditorView.theme({
      "&": {
        height: "100%",
        minHeight: "400px",
        overflow: "auto"
      },
      ".cm-scroller": {
        overflow: "auto",
        maxHeight: "100%"
      }
    });

    const state = EditorState.create({
      doc: initialValue,
      extensions: [...extensions, heightTheme]
    });

    const view = new EditorView({
      state,
      parent: container
    });

    setEditorView(view);
    return view;
  }, [isDarkTheme, handleMarkdownChange]);

  // 初始化CodeMirror编辑器
  useEffect(() => {
    if (editorMode === 'markdown') {
      const container = document.getElementById('codemirror-editor');
      if (container) {
        if (!editorView) {
          // 首次创建编辑器
          createCodeMirrorEditor(container, markdownContent);
        } else {
          // 更新现有编辑器的内容
          const currentValue = editorView.state.doc.toString();
          if (currentValue !== markdownContent) {
            editorView.dispatch({
              changes: {
                from: 0,
                to: editorView.state.doc.length,
                insert: markdownContent
              }
            });
          }
        }
      }
    }
  }, [editorMode, markdownContent, createCodeMirrorEditor, editorView]);

  // 主题切换时重新创建编辑器
  useEffect(() => {
    if (editorMode === 'markdown' && editorView) {
      const container = document.getElementById('codemirror-editor');
      if (container) {
        const currentValue = editorView.state.doc.toString();
        // 先销毁旧编辑器
        editorView.destroy();
        setEditorView(null);
        // 延迟创建新编辑器，避免冲突
        setTimeout(() => {
          createCodeMirrorEditor(container, currentValue);
        }, 100); // 增加延迟时间，确保DOM完全更新
      }
    }
  }, [isDarkTheme, createCodeMirrorEditor, editorMode]);

  // 清理编辑器
  useEffect(() => {
    return () => {
      if (editorView) {
        editorView.destroy();
      }
    };
  }, [editorView]);

  // 从 Markdown 文本中提取纯文本内容（用于字数统计）
  // 统计规则：只移除 Markdown 语法标记，保留所有文本内容和标点符号
  const getMarkdownPlainText = useCallback((markdown) => {
    if (!markdown) return '';
    
    let text = markdown;
    
    // 1. 处理代码块（```...```格式）
    // 提取代码块中的内容（包括标点符号），只移除代码块标记
    text = text.replace(/```[\w]*\n?([\s\S]*?)```/g, '$1');
    
    // 2. 处理行内代码（`...`格式）
    // 提取行内代码中的内容（包括标点符号），只移除行内代码标记
    text = text.replace(/`([^`\n]+)`/g, '$1');
    
    // 3. 移除标题标记（# 开头），保留标题文本和标点符号
    text = text.replace(/^#{1,6}\s+/gm, '');
    
    // 4. 移除粗体和斜体标记（**、*），保留文本和标点符号
    text = text.replace(/\*\*([^*]+?)\*\*/g, '$1');
    text = text.replace(/\*([^*\s][^*]*?[^*\s])\*/g, '$1');
    text = text.replace(/__([^_]+?)__/g, '$1');
    text = text.replace(/_([^_\s][^_]*?[^_\s])_/g, '$1');
    
    // 5. 移除链接标记（[文本](链接)），保留链接文本和标点符号
    text = text.replace(/\[([^\]]+)\]\([^\)]+\)/g, '$1');
    
    // 6. 移除图片标记（![alt](src)），保留图片描述文本和标点符号
    text = text.replace(/!\[([^\]]*)\]\([^\)]+\)/g, '$1');
    
    // 7. 移除引用标记（> 开头），保留引用内容和标点符号
    text = text.replace(/^>\s+/gm, '');
    
    // 8. 移除列表标记（-、*、+、数字.），保留列表内容和标点符号
    text = text.replace(/^[\s]*[-*+]\s+/gm, '');
    text = text.replace(/^[\s]*\d+\.\s+/gm, '');
    
    // 9. 移除水平线标记（仅当整行都是 - 或 *，且数量>=3时才移除）
    text = text.replace(/^[\s]*[-*]{3,}[\s]*$/gm, '');
    
    // 10. 处理表格标记（|）
    // 移除表格单元格之间的 | 分隔符，但保留单元格内容和标点符号
    // 移除表格分隔符行（只包含 |、-、:、空格的整行）
    text = text.replace(/^\|[\s]*:?-+:?[\s\|]*$/gm, ''); // 表格分隔符行
    text = text.replace(/\|/g, ' '); // 表格单元格分隔符替换为空格
    
    // 11. 移除多余的空白行（连续3个以上换行符替换为2个）
    text = text.replace(/\n{3,}/g, '\n\n');
    
    // 12. 移除首尾空白（但保留内部的所有内容和标点符号）
    text = text.trim();
    
    return text;
  }, []);

  // Markdown语法高亮函数
  const highlightMarkdown = useCallback((text) => {
    if (!text) return '';
    
    return text
      // 标题高亮
      .replace(/^(#{1,6})\s+(.+)$/gm, (match, hashes, content) => {
        const level = hashes.length;
        const color = isDarkTheme ? '#569cd6' : '#0969da';
        return `<span style="color: ${color}; font-weight: bold;">${hashes} ${content}</span>`;
      })
      // 粗体高亮
      .replace(/\*\*(.*?)\*\*/g, '<span style="font-weight: bold; color: ' + (isDarkTheme ? '#d4d4d4' : '#2c3e50') + ';">$1</span>')
      // 斜体高亮
      .replace(/\*(.*?)\*/g, '<span style="font-style: italic; color: ' + (isDarkTheme ? '#d4d4d4' : '#2c3e50') + ';">$1</span>')
      // 行内代码高亮
      .replace(/`([^`]+)`/g, (match, code) => {
        const bgColor = isDarkTheme ? '#2d2d2d' : '#f6f8fa';
        const textColor = isDarkTheme ? '#f8f8f2' : '#2c3e50';
        return `<span style="background-color: ${bgColor}; color: ${textColor}; padding: 0.2em 0.4em; border-radius: 3px; font-family: 'JetBrains Mono', 'Fira Code', 'Cascadia Code', 'SF Mono', Monaco, 'Consolas', 'Liberation Mono', 'Courier New', monospace;">${code}</span>`;
      })
      // 代码块高亮
      .replace(/```(\w+)?\n([\s\S]*?)```/g, (match, lang, code) => {
        const bgColor = isDarkTheme ? '#2d2d2d' : '#f6f8fa';
        const textColor = isDarkTheme ? '#f8f8f2' : '#2c3e50';
        return `<div style="background-color: ${bgColor}; color: ${textColor}; padding: 16px; border-radius: 6px; margin: 8px 0; font-family: 'JetBrains Mono', 'Fira Code', 'Cascadia Code', 'SF Mono', Monaco, 'Consolas', 'Liberation Mono', 'Courier New', monospace; white-space: pre-wrap;">${code.trim()}</div>`;
      })
      // 链接高亮
      .replace(/\[([^\]]+)\]\(([^)]+)\)/g, '<span style="color: ' + (isDarkTheme ? '#4fc3f7' : '#0969da') + '; text-decoration: underline;">$1</span>')
      // 列表项高亮
      .replace(/^(\s*)([-*+]|\d+\.)\s+(.+)$/gm, (match, indent, marker, content) => {
        const indentLevel = indent.length;
        const color = isDarkTheme ? '#d4d4d4' : '#2c3e50';
        return `${indent}<span style="color: ${isDarkTheme ? '#569cd6' : '#0969da'};">${marker}</span> <span style="color: ${color};">${content}</span>`;
      })
      // 引用高亮
      .replace(/^>\s*(.+)$/gm, (match, content) => {
        const borderColor = isDarkTheme ? '#404040' : '#dfe2e5';
        const textColor = isDarkTheme ? '#8b949e' : '#6a737d';
        return `<div style="border-left: 4px solid ${borderColor}; padding-left: 16px; margin: 8px 0; color: ${textColor};">${content}</div>`;
      });
  }, [isDarkTheme]);

  const wait3s = useCallback(() => {
    dirtyRef.current = true;
    if (timerId.current) clearTimeout(timerId.current);
    timerId.current = setTimeout(() => {
      form.submit();
    }, debounceDelay);
  }, [form, debounceDelay]);

  // 切换编辑器模式
  const handleModeChange = useCallback((mode) => {
    // 如果模式已锁定，不允许切换
    if (modeLocked) {
      message.warning('内容已保存，编辑器模式已锁定，无法切换');
      return;
    }
    
    if (mode === 'markdown') {
      // 切换到Markdown模式
      if (editorMode === 'rich') {
        // 从富文本模式切换到Markdown模式
        const currentContent = editorRef.current?.getContent() || data;
        const markdown = htmlToMarkdown(currentContent);
        setMarkdownContent(markdown);
        setLastMarkdownContent(markdown);
        setIsContentModified(false);
      }
      setEditorMode('markdown');
    } else {
      // 切换到富文本模式
      if (editorMode === 'markdown') {
        // 从Markdown模式切换到富文本模式
        if (isContentModified && markdownContent !== lastMarkdownContent) {
          const htmlContent = markdownToHtml(markdownContent, isDarkTheme);
          setData(htmlContent);
          if (editorRef.current) {
            editorRef.current.setContent(htmlContent);
          }
        }
      }
      setEditorMode('rich');
    }
  }, [editorMode, markdownContent, data, isContentModified, lastMarkdownContent, modeLocked]);

  // 处理Markdown内容变化
  const handleMarkdownChange = useCallback((value) => {
    setMarkdownContent(value);
    
    // 检查内容是否真的发生了变化
    const hasChanged = value !== lastMarkdownContent;
    setIsContentModified(hasChanged);
    
    // 添加到历史记录
    if (hasChanged) {
      const newHistory = markdownHistory.slice(0, historyIndex + 1);
      newHistory.push(value);
      setMarkdownHistory(newHistory);
      setHistoryIndex(newHistory.length - 1);
    }
    
    // 在Markdown模式下，直接保存Markdown格式到表单
    // 不需要转换为HTML，因为保存时会根据模式决定格式
    form.setFieldsValue({pubContent: value});
    
    // 触发自动保存（与富文本模式保持一致，每次内容变化都触发防抖）
    wait3s();
  }, [form, lastMarkdownContent, markdownHistory, historyIndex, wait3s]);

  // 处理富文本内容变化，自动生成标题ID
  const handleRichTextChange = useCallback((content) => {
    setData(content);
    
    // 自动为标题添加ID
    const tempDiv = document.createElement('div');
    tempDiv.innerHTML = content;
    const headings = tempDiv.querySelectorAll('h1, h2, h3, h4, h5, h6');
    
    headings.forEach((heading, index) => {
      if (!heading.id) {
        heading.id = `heading-${index}`;
      }
    });
    
    // 更新内容
    const processedContent = tempDiv.innerHTML;
    if (processedContent !== content) {
      setData(processedContent);
      if (editorRef.current) {
        editorRef.current.setContent(processedContent);
      }
    }
    
    wait3s();
  }, [wait3s]);

  // 撤销操作
  const handleUndo = useCallback(() => {
    if (historyIndex > 0) {
      const newIndex = historyIndex - 1;
      setHistoryIndex(newIndex);
      setMarkdownContent(markdownHistory[newIndex]);
      form.setFieldsValue({pubContent: markdownHistory[newIndex]});
    }
  }, [historyIndex, markdownHistory, form]);

  // 重做操作
  const handleRedo = useCallback(() => {
    if (historyIndex < markdownHistory.length - 1) {
      const newIndex = historyIndex + 1;
      setHistoryIndex(newIndex);
      setMarkdownContent(markdownHistory[newIndex]);
      form.setFieldsValue({pubContent: markdownHistory[newIndex]});
    }
  }, [historyIndex, markdownHistory, form]);

  // 预览功能
  const handlePreview = useCallback(() => {
    let content = '';
    if (editorMode === 'markdown') {
      content = markdownContent;
    } else {
      content = editorRef.current?.getContent() || data;
    }
    
    // 使用类似 TinyMCE 的对话框预览
    const showDialog = () => {
      // 创建遮罩层
      const backdrop = document.createElement('div');
      backdrop.className = 'tox-dialog-wrap__backdrop';
      backdrop.style.cssText = `
        position: fixed;
        top: 0;
        left: 0;
        width: 100%;
        height: 100%;
        background-color: rgba(0, 0, 0, 0.5);
        z-index: 999999;
      `;
      
      // 创建对话框
      const dialog = document.createElement('div');
      dialog.className = 'tox-dialog-wrap';
      dialog.style.cssText = `
        position: fixed;
        top: 0;
        left: 0;
        width: 100%;
        height: 100%;
        z-index: 999999;
        display: flex;
        align-items: center;
        justify-content: center;
      `;
      
      // 创建对话框内容
      const dialogContent = document.createElement('div');
      dialogContent.className = 'tox-dialog';
      dialogContent.style.cssText = `
        background: white;
        border-radius: 8px;
        box-shadow: 0 4px 20px rgba(0, 0, 0, 0.15);
        width: 80%;
        max-width: 800px;
        height: 80%;
        max-height: 600px;
        display: flex;
        flex-direction: column;
        overflow: hidden;
      `;
      
      // 创建标题栏
      const header = document.createElement('div');
      header.className = 'tox-dialog__header';
      header.style.cssText = `
        padding: 16px 20px;
        border-bottom: 1px solid #e8e8e8;
        display: flex;
        justify-content: space-between;
        align-items: center;
        background: #fafafa;
      `;
      
      // 全屏状态
      let isFullscreen = false;
      
      const toggleFullscreen = () => {
        isFullscreen = !isFullscreen;
        if (isFullscreen) {
          dialogContent.style.cssText = `
            background: white;
            border-radius: 0;
            box-shadow: none;
            width: 100vw;
            height: 100vh;
            max-width: none;
            max-height: none;
            display: flex;
            flex-direction: column;
            overflow: hidden;
            position: fixed;
            top: 0;
            left: 0;
            z-index: 1000000;
          `;
          fullscreenBtn.innerHTML = '⤓';
          fullscreenBtn.title = '退出全屏';
        } else {
          dialogContent.style.cssText = `
            background: white;
            border-radius: 8px;
            box-shadow: 0 4px 20px rgba(0, 0, 0, 0.15);
            width: 80%;
            max-width: 800px;
            height: 80%;
            max-height: 600px;
            display: flex;
            flex-direction: column;
            overflow: hidden;
          `;
          fullscreenBtn.innerHTML = '⤢';
          fullscreenBtn.title = '全屏';
        }
      };
      
      const fullscreenBtn = document.createElement('button');
      fullscreenBtn.innerHTML = '⤢';
      fullscreenBtn.title = '全屏';
      fullscreenBtn.style.cssText = `
        background: #1890ff;
        color: white;
        border: none;
        padding: 6px 12px;
        border-radius: 4px;
        cursor: pointer;
        font-size: 16px;
        margin-right: 8px;
        width: 32px;
        height: 32px;
        display: flex;
        align-items: center;
        justify-content: center;
      `;
      fullscreenBtn.onclick = toggleFullscreen;
      
      header.innerHTML = `
        <div class="tox-dialog__title">内容预览</div>
        <div style="display: flex; align-items: center;">
          <button type="button" class="tox-button tox-button--icon" onclick="closeDialog()" style="background: none; border: none; cursor: pointer;">&times;</button>
        </div>
      `;
      header.querySelector('div:last-child').insertBefore(fullscreenBtn, header.querySelector('div:last-child').firstChild);
      
      // 创建内容区域
      const body = document.createElement('div');
      body.className = 'tox-dialog__body';
      body.style.cssText = `
        flex: 1;
        padding: 20px;
        overflow-y: auto;
        font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, sans-serif;
        line-height: 1.6;
        color: #2c3e50;
      `;
      
      // 创建底部按钮区域
      const footer = document.createElement('div');
      footer.className = 'tox-dialog__footer';
      footer.style.cssText = `
        padding: 16px 20px;
        border-top: 1px solid #e8e8e8;
        display: flex;
        justify-content: flex-end;
        background: #fafafa;
      `;
      
      const closeButton = document.createElement('button');
      closeButton.className = 'tox-button';
      closeButton.textContent = '关闭';
      closeButton.style.cssText = `
        background: #1890ff;
        color: white;
        border: none;
        padding: 8px 16px;
        border-radius: 4px;
        cursor: pointer;
        font-size: 14px;
      `;
      
      // 创建关闭函数
      window.closeDialog = () => {
        document.body.removeChild(backdrop);
        document.body.removeChild(dialog);
        delete window.closeDialog;
      };
      
      // 绑定关闭事件
      closeButton.onclick = window.closeDialog;
      backdrop.onclick = window.closeDialog;
      
      // 渲染内容
      if (editorMode === 'markdown') {
        // 使用 ReactMarkdown 渲染，与文档模块保持一致
        const markdownElement = React.createElement(ReactMarkdown, {
          remarkPlugins: [remarkGfm],
          rehypePlugins: [rehypeRaw],
          className: "markdown-body",
          components: {
            img({src, alt, title, ...props}) {
              return React.createElement(MarkdownImage, {
                src: src,
                alt: alt,
                title: title,
                content: content,
                ...props
              });
            },
            code({node, inline, className, children, ...props}) {
              const match = /language-(\w+)/.exec(className || '');
              
              return !inline && match ? (
                React.createElement('div', {
                  style: { 
                    backgroundColor: isDarkTheme ? '#0d1117' : '#f6f8fa',
                    borderRadius: '6px',
                    padding: '16px',
                    margin: '16px 0',
                    overflow: 'auto'
                  }
                }, React.createElement(SyntaxHighlighter, {
                  style: isDarkTheme ? vscDarkPlus : vs,
                  language: match[1],
                  PreTag: "div",
                  showLineNumbers: false,
                  wrapLines: true,
                  wrapLongLines: true,
                  customStyle: {
                    background: 'transparent',
                    margin: 0,
                    padding: 0,
                    border: 'none',
                    borderRadius: 0
                  },
                  ...props
                }, String(children).replace(/\n$/, '')))
              ) : (
                React.createElement('code', { className: className, ...props }, children)
              );
            }
          }
        }, content);
        
        // 渲染为 HTML 字符串
        const html = renderToString(markdownElement);
        
        body.innerHTML = `
          <style>
            .markdown-body {
              font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif;
              font-size: 16px;
              line-height: 1.6;
              color: #24292e;
              max-width: none;
              margin: 0;
              padding: 20px;
            }
            .markdown-body h1, .markdown-body h2, .markdown-body h3, .markdown-body h4, .markdown-body h5, .markdown-body h6 {
              margin-top: 24px;
              margin-bottom: 16px;
              font-weight: 600;
              line-height: 1.25;
            }
            .markdown-body h1 { font-size: 2em; border-bottom: 1px solid #eaecef; padding-bottom: 0.3em; }
            .markdown-body h2 { font-size: 1.5em; border-bottom: 1px solid #eaecef; padding-bottom: 0.3em; }
            .markdown-body h3 { font-size: 1.25em; }
            .markdown-body p { margin-bottom: 16px; }
            .markdown-body ul, .markdown-body ol { margin-bottom: 16px; padding-left: 2em; }
            .markdown-body li { margin-bottom: 4px; }
            .markdown-body strong { font-weight: 600; }
            .markdown-body em { font-style: italic; }
            .markdown-body code { padding: 0.2em 0.4em; background-color: rgba(27, 31, 35, 0.05); border-radius: 3px; font-family: 'Monaco', 'Menlo', 'Ubuntu Mono', monospace; }
            .markdown-body pre { background-color: #f6f8fa; border-radius: 6px; padding: 16px; overflow: auto; }
            .markdown-body pre code {
              background: transparent;
              padding: 0;
              color: #24292e;
              font-family: 'JetBrains Mono', 'Fira Code', 'Cascadia Code', 'SF Mono', Monaco, 'Consolas', 'Liberation Mono', 'Courier New', monospace;
              display: block;
              white-space: pre;
            }
            .markdown-body a {
              color: #0366d6;
              text-decoration: none;
            }
            .markdown-body a:hover {
              text-decoration: underline;
            }
            .markdown-body blockquote {
              border-left: 4px solid #dfe2e5;
              padding-left: 16px;
              margin: 16px 0;
              color: #6a737d;
            }
            .markdown-body table {
              border-collapse: collapse;
              width: 100%;
              margin: 16px 0;
            }
            .markdown-body th, .markdown-body td {
              border: 1px solid #dfe2e5;
              padding: 8px 12px;
              text-align: left;
            }
            .markdown-body th {
              background-color: #f6f8fa;
              font-weight: 600;
            }
          </style>
          <div class="markdown-body">${html}</div>
        `;
      } else {
        body.innerHTML = `<div class="preview-content">${content}</div>`;
      }
      
      // 组装对话框
      dialogContent.appendChild(header);
      dialogContent.appendChild(body);
      footer.appendChild(closeButton);
      dialogContent.appendChild(footer);
      dialog.appendChild(dialogContent);
      
      // 添加到页面
      document.body.appendChild(backdrop);
      document.body.appendChild(dialog);
    };
    
    showDialog();
  }, [editorMode, markdownContent, data, isDarkTheme]);

  useEffect(() => {
    if (currentChapter && currentChapter.id) {
      form.setFieldsValue({
        id: currentChapter.id,
        parentId: currentChapter.parentId,
        pubTitle: currentChapter.pubTitle,
        pubContent: currentChapter.pubContent,
        pubStatus: currentChapter.pubStatus,
      });
      setData(currentChapter.pubContent);
      
      // 根据内容判断是否为新建内容
      const content = currentChapter.pubContent || '';
      const mimeType = currentChapter.pubMimeType;
      const hasContent = content.trim() !== '';
      
      setIsNewContent(!hasContent);
      
      if (!hasContent) {
        // 无内容：默认富文本模式，可以切换
        setEditorMode('rich');
        setModeLocked(false);
        setIsContentModified(false);
      } else {
        // 有内容：根据MIME类型锁定模式
        setModeLocked(true);
        
        if (mimeType === 'text/markdown') {
          // Markdown内容：锁定为Markdown模式
          setMarkdownContent(content);
          setLastMarkdownContent(content);
          setEditorMode('markdown');
          setIsContentModified(false);
          // 初始化历史记录
          setMarkdownHistory([content]);
          setHistoryIndex(0);
        } else {
          // HTML内容或MIME为空：锁定为富文本模式
          setData(content);
          setEditorMode('rich');
          setIsContentModified(false);
        }
      }
    }
  }, [currentChapter]);

  useEffect(() => {
    form.setFieldsValue({pubContent: data});
    wait3s();
  }, [data]);

  // 根据可见性/焦点动态调整节流；恢复可见时若存在未提交的改动则立即保存一次
  useEffect(() => {
    const handleVisibilityOrFocus = () => {
      const hidden = document.hidden;
      setDebounceDelay(hidden ? BACKGROUND_DELAY : FOREGROUND_DELAY);
      if (!hidden && dirtyRef.current) {
        // 立刻补一次保存
        if (timerId.current) clearTimeout(timerId.current);
        form.submit();
      }
    };
    document.addEventListener('visibilitychange', handleVisibilityOrFocus);
    window.addEventListener('focus', handleVisibilityOrFocus);
    window.addEventListener('blur', handleVisibilityOrFocus);
    return () => {
      document.removeEventListener('visibilitychange', handleVisibilityOrFocus);
      window.removeEventListener('focus', handleVisibilityOrFocus);
      window.removeEventListener('blur', handleVisibilityOrFocus);
    };
  }, [form]);

  const doSave = useCallback(async (values) => {
    // 前置校验：无 id 或无标题或内容未变化时，不触发保存，也不提示错误
    if (!values?.id || !values?.pubTitle) {
      return;
    }
    if ((values?.pubContent === currentChapter?.pubContent) && (values?.pubTitle === currentChapter?.pubTitle)) {
      return;
    }

    setSaveStatus('saving');
    try {
      // 根据当前编辑器模式决定保存格式
      let saveValues = {...values};
      
      if (editorMode === 'markdown') {
        // Markdown模式：保存Markdown格式
        saveValues.pubContent = markdownContent;
        saveValues.pubMimeType = 'text/markdown'; // 使用pub_mime_type字段标记内容类型
      } else if (editorMode === 'preview') {
        // 预览模式：如果内容被修改，保存Markdown格式；否则保存HTML格式
        if (isContentModified) {
          saveValues.pubContent = markdownContent;
          saveValues.pubMimeType = 'text/markdown';
        } else {
          saveValues.pubContent = values.pubContent;
          saveValues.pubMimeType = 'text/html';
        }
      } else {
        // 富文本模式：保存HTML格式
        saveValues.pubContent = values.pubContent;
        saveValues.pubMimeType = 'text/html'; // 使用pub_mime_type字段标记内容类型
      }

      const res = await handleSave(saveValues, currentChapter);
      if (!res) {
        setSaveStatus('error');
        return;
      }

      setSaveStatus('saved');
      setLastSaveTime(new Date());
      dirtyRef.current = false;
      
      // 更新最后保存的内容
      if (editorMode === 'markdown') {
        setLastMarkdownContent(markdownContent);
      }
      
      // 保存后锁定编辑器模式
      setModeLocked(true);
      setIsNewContent(false);

      !isSingle ? dispatch({ // 转发可以跳过effects，直接请求reducer
        type: 'bookSpace/updateCurChapter',
        payload: {data: saveValues},
      }) : dispatch({
        type: 'bookSpace/updateCurBook',
        payload: {data: saveValues},
      });

      // 3秒后重置状态
      setTimeout(() => setSaveStatus('idle'), 3000);
    } catch (error) {
      setSaveStatus('error');
      setTimeout(() => setSaveStatus('idle'), 3000);
    }
  }, [currentChapter, dispatch, isSingle, editorMode, markdownContent, isContentModified]);

  const inputProp = {
    style: {
      width: '100%', padding: '0 80px 10px 40px', marginBottom: 0,
      fontSize: '30px', lineHeight: '30px', boxShadow: 'none',
      outline: 'none', overflow: 'none', whiteSpace: 'nowrap', textOverflow: 'ellipsis'
    },
  };

  const checkTitle = useCallback((_, value) => {
    const promise = Promise;

    if (!value) {
      return message.warn('请输入标题').then(() => promise.reject());
    }

    if (value.length >= 120) {
      return message.warn('标题字数不能超过120').then(() => promise.reject());
    }

    return promise.resolve();
  }, []);

  // 保存状态提示：为避免编辑器频繁打断焦点，仅在出错时提示
  const saveStatusAlert = useMemo(() => {
    if (saveStatus !== 'error') return null;
    return (
      <Alert
        type="error"
        message={'保存失败，请重试'}
        icon={<SaveOutlined />}
        showIcon
        style={{ marginBottom: 16 }}
        closable={false}
      />
    );
  }, [saveStatus]);

  // 渲染编辑器内容
  const renderEditorContent = () => {
    if (editorMode === 'markdown') {
      return (
        <div style={{ 
          flex: 1,
          border: '1px solid #d9d9d9',
          display: 'flex',
          flexDirection: 'column',
          height: 'calc(100vh - 41px - 39px - 16px)',
          ...(isFullscreen && {
            position: 'fixed',
            top: 0,
            left: 0,
            right: 0,
            bottom: 0,
            zIndex: 9999,
            backgroundColor: '#fff',
            height: '100vh',
            width: '100vw',
            // 移动端适配
            minHeight: '100vh',
            maxHeight: '100vh',
            overflow: 'hidden'
          })
        }}>
          {/* Markdown工具栏 */}
          <div style={{
            borderBottom: '1px solid #d9d9d9',
            padding: '0',
            backgroundColor: '#fff',
            display: 'flex',
            height: '39px',
            gap: '0',
            alignItems: 'center',
            flexWrap: 'nowrap',
            overflowX: 'auto',
            overflowY: 'hidden',
            WebkitOverflowScrolling: 'touch'
          }}>
            {/* 格式化组 - 粗体和斜体 */}
            <div style={{
              display: 'flex',
              alignItems: 'center',
              gap: '0',
              padding: '8px 4px'
            }}>
              <Button 
                size="large" 
                icon={<BoldOutlined />}
                title="粗体"
                style={{
                  border: 'none',
                  background: 'transparent',
                  boxShadow: 'none',
                  padding: '2px 6px',
                  fontSize: '32px',
                  fontWeight: 'bold',
                  width: '36px',
                  height: '36px',
                  color: '#222f3e'
                }}
                onMouseEnter={(e) => {
                  e.target.style.backgroundColor = '#f5f5f5';
                }}
                onMouseLeave={(e) => {
                  e.target.style.backgroundColor = 'transparent';
                }}
                onClick={() => {
                  if (editorView) {
                    const selection = editorView.state.selection;
                    const selectedText = editorView.state.doc.sliceString(selection.main.from, selection.main.to);
                    const newText = `**${selectedText}**`;
                    editorView.dispatch({
                      changes: {
                        from: selection.main.from,
                        to: selection.main.to,
                        insert: newText
                      }
                    });
                  }
                }}
              />
              <Button 
                size="large" 
                icon={<ItalicOutlined />}
                title="斜体"
                style={{
                  border: 'none',
                  background: 'transparent',
                  boxShadow: 'none',
                  padding: '2px 6px',
                  fontSize: '32px',
                  fontWeight: 'bold',
                  width: '36px',
                  height: '36px',
                  color: '#222f3e'
                }}
                onMouseEnter={(e) => {
                  e.target.style.backgroundColor = '#f5f5f5';
                }}
                onMouseLeave={(e) => {
                  e.target.style.backgroundColor = 'transparent';
                }}
                onClick={() => {
                  if (editorView) {
                    const selection = editorView.state.selection;
                    const selectedText = editorView.state.doc.sliceString(selection.main.from, selection.main.to);
                    const newText = `*${selectedText}*`;
                    editorView.dispatch({
                      changes: {
                        from: selection.main.from,
                        to: selection.main.to,
                        insert: newText
                      }
                    });
                  }
                }}
              />
            </div>
            
            {/* 链接和图片组 */}
            <div style={{
              display: 'flex',
              alignItems: 'center',
              gap: '0',
              padding: '8px 4px',
              borderLeft: '1px solid #d9d9d9'
            }}>
              <Button 
                size="large" 
                icon={<LinkOutlined />}
                title="链接"
                style={{
                  border: 'none',
                  background: 'transparent',
                  boxShadow: 'none',
                  padding: '2px 6px',
                  fontSize: '32px',
                  fontWeight: 'bold',
                  width: '36px',
                  height: '36px',
                  color: '#222f3e'
                }}
                onMouseEnter={(e) => {
                  e.target.style.backgroundColor = '#f5f5f5';
                }}
                onMouseLeave={(e) => {
                  e.target.style.backgroundColor = 'transparent';
                }}
                onClick={() => {
                  if (editorView) {
                    const selection = editorView.state.selection;
                    const selectedText = editorView.state.doc.sliceString(selection.main.from, selection.main.to);
                    const newText = `[${selectedText}](链接地址)`;
                    editorView.dispatch({
                      changes: {
                        from: selection.main.from,
                        to: selection.main.to,
                        insert: newText
                      }
                    });
                  }
                }}
              />
              <Button 
                size="large" 
                icon={<PictureOutlined />}
                title="图片"
                style={{
                  border: 'none',
                  background: 'transparent',
                  boxShadow: 'none',
                  padding: '2px 6px',
                  fontSize: '32px',
                  fontWeight: 'bold',
                  width: '36px',
                  height: '36px',
                  color: '#222f3e'
                }}
                onMouseEnter={(e) => {
                  e.target.style.backgroundColor = '#f5f5f5';
                }}
                onMouseLeave={(e) => {
                  e.target.style.backgroundColor = 'transparent';
                }}
                onClick={() => {
                  // 显示图片插入/编辑对话框
                  showMarkdownImageDialog(null, form, editorView);
                }}
              />
            </div>
            
            {/* 为markdown模式添加图片右键编辑功能 */}
            {editorMode === 'markdown' && (
              <script dangerouslySetInnerHTML={{
                __html: `
                  setTimeout(() => {
                    const markdownContent = document.querySelector('#codemirror-editor textarea');
                    if (markdownContent) {
                      markdownContent.addEventListener('contextmenu', (e) => {
                        const text = markdownContent.value;
                        const cursorPos = markdownContent.selectionStart;
                        
                        // 检查光标位置是否在图片标签内
                        const beforeCursor = text.substring(0, cursorPos);
                        
                        // 查找最近的图片标签
                        const imgMatch = beforeCursor.match(/<img[^>]*src="([^"]*)"[^>]*>/);
                        const markdownImgMatch = beforeCursor.match(/!\\[([^\\]]*)\\]\\(([^)]+)\\)/);
                        
                        if (imgMatch || markdownImgMatch) {
                          e.preventDefault();
                          
                          // 创建右键菜单
                          const contextMenu = document.createElement('div');
                          contextMenu.style.cssText = \`
                            position: fixed;
                            top: \${e.clientY}px;
                            left: \${e.clientX}px;
                            background: white;
                            border: 1px solid #d9d9d9;
                            border-radius: 4px;
                            box-shadow: 0 2px 8px rgba(0,0,0,0.15);
                            z-index: 999999;
                            padding: 4px 0;
                          \`;
                          
                          const editItem = document.createElement('div');
                          editItem.textContent = '编辑图片';
                          editItem.style.cssText = \`
                            padding: 8px 16px;
                            cursor: pointer;
                            font-size: 14px;
                          \`;
                          editItem.onmouseover = () => editItem.style.backgroundColor = '#f5f5f5';
                          editItem.onmouseout = () => editItem.style.backgroundColor = 'transparent';
                          
                          editItem.onclick = () => {
                            document.body.removeChild(contextMenu);
                            
                            // 获取图片信息
                            let imageUrl, imageAlt, imageWidth, imageHeight, imageName;
                            if (imgMatch) {
                              imageUrl = imgMatch[1];
                              const altMatch = imgMatch[0].match(/alt="([^"]*)"/);
                              imageAlt = altMatch ? altMatch[1] : '';
                              const widthMatch = imgMatch[0].match(/width="([^"]*)"/);
                              imageWidth = widthMatch ? widthMatch[1] : '';
                              const heightMatch = imgMatch[0].match(/height="([^"]*)"/);
                              imageHeight = heightMatch ? heightMatch[1] : '';
                              imageName = '';
                            } else if (markdownImgMatch) {
                              imageAlt = markdownImgMatch[1];
                              imageUrl = markdownImgMatch[2];
                              imageWidth = '';
                              imageHeight = '';
                              // 提取markdown中的title（图片名称）
                              const titleMatch = markdownImgMatch[0].match(/\\[([^\\]]*)\\]\\(([^)]+)\\) "([^"]*)"/);
                              imageName = titleMatch ? titleMatch[3] : '';
                            }
                            
                            // 显示编辑对话框
                            if (window.showMarkdownImageDialog) {
                              window.showMarkdownImageDialog({
                                url: imageUrl,
                                alt: imageAlt,
                                width: imageWidth,
                                height: imageHeight,
                                name: imageName
                              }, window.currentForm, window.currentEditorView);
                            }
                          };
                          
                          contextMenu.appendChild(editItem);
                          document.body.appendChild(contextMenu);
                          
                          // 点击其他地方关闭菜单
                          const closeMenu = (e) => {
                            if (!contextMenu.contains(e.target)) {
                              document.body.removeChild(contextMenu);
                              document.removeEventListener('click', closeMenu);
                            }
                          };
                          setTimeout(() => document.addEventListener('click', closeMenu), 0);
                        }
                      });
                    }
                  }, 1000);
                `
              }} />
            )}
            
            {/* 列表和代码组 */}
            <div style={{
              display: 'flex',
              alignItems: 'center',
              gap: '0',
              padding: '8px 4px',
              borderLeft: '1px solid #d9d9d9'
            }}>
              <Button 
                size="large" 
                icon={<UnorderedListOutlined />}
                title="列表"
                style={{
                  border: 'none',
                  background: 'transparent',
                  boxShadow: 'none',
                  padding: '2px 6px',
                  fontSize: '32px',
                  fontWeight: 'bold',
                  width: '36px',
                  height: '36px',
                  color: '#222f3e'
                }}
                onMouseEnter={(e) => {
                  e.target.style.backgroundColor = '#f5f5f5';
                }}
                onMouseLeave={(e) => {
                  e.target.style.backgroundColor = 'transparent';
                }}
                onClick={() => {
                  if (editorView) {
                    const doc = editorView.state.doc;
                    const newText = '\n\n- 列表项1\n- 列表项2\n- 列表项3\n';
                    editorView.dispatch({
                      changes: {
                        from: doc.length,
                        to: doc.length,
                        insert: newText
                      }
                    });
                  } else {
                    const newContent = markdownContent + '\n\n- 列表项1\n- 列表项2\n- 列表项3\n';
                    handleMarkdownChange(newContent);
                  }
                }}
              />
              <Button 
                size="large" 
                icon={<CodeOutlined />}
                title="代码"
                style={{
                  border: 'none',
                  background: 'transparent',
                  boxShadow: 'none',
                  padding: '2px 6px',
                  fontSize: '32px',
                  fontWeight: 'bold',
                  width: '36px',
                  height: '36px',
                  color: '#222f3e'
                }}
                onMouseEnter={(e) => {
                  e.target.style.backgroundColor = '#f5f5f5';
                }}
                onMouseLeave={(e) => {
                  e.target.style.backgroundColor = 'transparent';
                }}
                onClick={() => {
                  if (editorView) {
                    const doc = editorView.state.doc;
                    const newText = '\n\n```\n代码块\n```\n';
                    editorView.dispatch({
                      changes: {
                        from: doc.length,
                        to: doc.length,
                        insert: newText
                      }
                    });
                  } else {
                    const newContent = markdownContent + '\n\n```\n代码块\n```\n';
                    handleMarkdownChange(newContent);
                  }
                }}
              />
            </div>
            
            {/* 操作组 */}
            <div style={{
              display: 'flex',
              alignItems: 'center',
              gap: '0',
              padding: '8px 4px',
              borderLeft: '1px solid #d9d9d9'
            }}>
              <Button 
                size="large" 
                icon={<UndoOutlined />}
                title="撤销"
                style={{
                  border: 'none',
                  background: 'transparent',
                  boxShadow: 'none',
                  padding: '2px 6px',
                  fontSize: '32px',
                  fontWeight: 'bold',
                  width: '36px',
                  height: '36px',
                  color: '#222f3e'
                }}
                onMouseEnter={(e) => {
                  e.target.style.backgroundColor = '#f5f5f5';
                }}
                onMouseLeave={(e) => {
                  e.target.style.backgroundColor = 'transparent';
                }}
                onClick={handleUndo}
                disabled={historyIndex <= 0}
              />
              <Button 
                size="large" 
                icon={<RedoOutlined />}
                title="重做"
                style={{
                  border: 'none',
                  background: 'transparent',
                  boxShadow: 'none',
                  padding: '2px 6px',
                  fontSize: '32px',
                  fontWeight: 'bold',
                  width: '36px',
                  height: '36px',
                  color: '#222f3e'
                }}
                onMouseEnter={(e) => {
                  e.target.style.backgroundColor = '#f5f5f5';
                }}
                onMouseLeave={(e) => {
                  e.target.style.backgroundColor = 'transparent';
                }}
                onClick={handleRedo}
                disabled={historyIndex >= markdownHistory.length - 1}
              />
            </div>
            
            {/* 主题切换组 */}
            <div style={{
              display: 'flex',
              alignItems: 'center',
              gap: '0',
              padding: '8px 4px',
              borderLeft: '1px solid #d9d9d9'
            }}>
              <Button 
                size="large" 
                icon={isDarkTheme ? <SunOutlined /> : <MoonOutlined />}
                title={isDarkTheme ? "切换到浅色主题" : "切换到深色主题"}
                style={{
                  border: 'none',
                  background: 'transparent',
                  boxShadow: 'none',
                  padding: '2px 6px',
                  fontSize: '32px',
                  fontWeight: 'bold',
                  width: '36px',
                  height: '36px',
                  color: '#222f3e'
                }}
                onMouseEnter={(e) => {
                  e.target.style.backgroundColor = '#f5f5f5';
                }}
                onMouseLeave={(e) => {
                  e.target.style.backgroundColor = 'transparent';
                }}
                onClick={toggleTheme}
              />
              <Button 
                size="large" 
                icon={<QuestionCircleOutlined />}
                title="帮助"
                style={{
                  border: 'none',
                  background: 'transparent',
                  boxShadow: 'none',
                  padding: '2px 6px',
                  fontSize: '32px',
                  fontWeight: 'bold',
                  width: '36px',
                  height: '36px',
                  color: '#222f3e'
                }}
                onMouseEnter={(e) => {
                  e.target.style.backgroundColor = '#f5f5f5';
                }}
                onMouseLeave={(e) => {
                  e.target.style.backgroundColor = 'transparent';
                }}
                onClick={() => {
                  Modal.info({
                    title: 'Markdown语法帮助',
                    content: (
                      <div>
                        <h4>基本语法：</h4>
                        <ul>
                          <li><strong>粗体</strong>: **文本** 或 __文本__</li>
                          <li><em>斜体</em>: *文本* 或 _文本_</li>
                          <li>链接: [文本](URL)</li>
                          <li>图片: ![描述](图片URL)</li>
                          <li>标题: # 一级标题 ## 二级标题</li>
                          <li>列表: - 项目 或 1. 项目</li>
                          <li>代码: `行内代码` 或 ```代码块```</li>
                          <li>引用: &gt; 引用文本</li>
                          <li>分割线: --- 或 ***</li>
                        </ul>
                      </div>
                    ),
                    width: 600
                  });
                }}
              />
            </div>
            
            {/* 全屏组 */}
            <div style={{
              display: 'flex',
              alignItems: 'center',
              gap: '0',
              padding: '8px 4px',
              borderLeft: '1px solid #d9d9d9'
            }}>
              <Button 
                size="large" 
                icon={isFullscreen ? <FullscreenExitOutlined /> : <FullscreenOutlined />}
                title={isFullscreen ? "退出全屏" : "全屏"}
                style={{
                  border: 'none',
                  background: 'transparent',
                  boxShadow: 'none',
                  padding: '2px 6px',
                  fontSize: '32px',
                  fontWeight: 'bold',
                  width: '36px',
                  height: '36px',
                  color: '#222f3e'
                }}
                onMouseEnter={(e) => {
                  e.target.style.backgroundColor = '#f5f5f5';
                }}
                onMouseLeave={(e) => {
                  e.target.style.backgroundColor = 'transparent';
                }}
                onClick={toggleFullscreen}
              />
            </div>
            {/* 预览组 - 移到最后 */}
            <div style={{
              display: 'flex',
              alignItems: 'center',
              gap: '0',
              padding: '8px 4px',
              borderLeft: '1px solid #d9d9d9'
            }}>
              <Button 
                size="large" 
                icon={<PreviewOutlined />}
                title="预览"
                style={{
                  border: 'none',
                  background: 'transparent',
                  boxShadow: 'none',
                  padding: '2px 6px',
                  fontSize: '32px',
                  fontWeight: 'bold',
                  width: '36px',
                  height: '36px',
                  color: '#222f3e'
                }}
                onMouseEnter={(e) => {
                  e.target.style.backgroundColor = '#f5f5f5';
                }}
                onMouseLeave={(e) => {
                  e.target.style.backgroundColor = 'transparent';
                }}
                onClick={handlePreview}
              />
            </div>
          </div>
          
          {/* CodeMirror编辑器容器 */}
          <div 
            id="codemirror-editor"
            style={{ 
              width: '100%', 
              border: 'none',
              outline: 'none',
              overflow: 'hidden',
              minHeight: 0,
                height: isFullscreen ? 'calc(100vh - 96px)' : 'calc(100% - 96px)' // 全屏模式下使用100vh
            }}
          />
          
          {/* Markdown状态栏 */}
          <div style={{
            borderTop: '1px solid #d9d9d9',
            backgroundColor: '#f5f5f5',
            padding: '4px 8px',
            display: 'flex',
            justifyContent: 'space-between',
            alignItems: 'center',
            fontSize: '12px',
            color: '#666',
            height: '24px',
            ...(isFullscreen && {
              position: 'fixed',
              bottom: 0,
              left: 0,
              right: 0,
              zIndex: 10000,
              backgroundColor: '#f5f5f5'
            })
          }}>
            <span style={{ fontWeight: 'bold' }}>m</span>
            <span>{getMarkdownPlainText(markdownContent).length}字</span>
          </div>
        </div>
      );
    } else {
      return (
        <Editor
          onInit={(evt, editor) => {
            editorRef.current = editor;
          }}
          init={{
            height: 'calc(100vh - 41px - 32px)',
            menubar: true,
            language: 'zh_CN',
            branding: false,
            resize: true,
            statusbar: true,
            object_resizing: 'img',
            images_upload_handler: (blobInfo, success, failure) => {
              imgUploadHandler(blobInfo, success, failure, form);
            },
            convert_urls: false,
            file_picker_types: 'media',
            file_picker_callback: (callback, value, meta) => {
              filePickerCallBack(callback, value, meta, form);
            },
            mobile: {
              width: '100%',
              height: 'calc(100vh - 150px)',
              menubar: true,
              resize: true,
              object_resizing: 'img',
              toolbar: ['formatselect | fullscreen image media bold italic bullist numlist | alignleft aligncenter alignright alignjustify link unlink undo redo | fontsizeselect paste publish',
              ],
            },
            plugins: [
              'advlist autolink lists link image charmap print preview anchor',
              'searchreplace visualblocks code fullscreen',
              'insertdatetime media table paste wordcount help',
              'importcss save directionality visualchars template codesample hr pagebreak nonbreaking toc imagetools textpattern noneditable quickbars emoticons'
            ],
            paste_data_images: true,
            toolbar_sticky: true,
            toolbar_sticky_offset: 0,
            toolbar: ['formatselect | bold italic | forecolor backcolor | bullist numlist | alignleft aligncenter alignright alignjustify | link unlink | undo redo | fontsizeselect | paste removeformat | code | preview | fullscreen publish',
            ],
            setup(ed) {
              // 添加预览按钮
              ed.ui.registry.addButton('preview', {
                text: '预览',
                tooltip: '预览内容',
                onAction: () => {
                  handlePreview();
                }
              });
              
              // 自动生成标题ID功能
              ed.on('NodeChange', (e) => {
                const node = e.element;
                if (node && node.tagName && /^H[1-6]$/.test(node.tagName)) {
                  if (!node.id) {
                    const text = node.textContent || node.innerText || '';
                    const id = text.toLowerCase()
                      .replace(/[^\u4e00-\u9fa5a-zA-Z0-9\s]/g, '') // 只保留中文、英文、数字和空格
                      .replace(/\s+/g, '-') // 空格替换为连字符
                      .substring(0, 50); // 限制长度
                    if (id) {
                      node.id = id;
                    }
                  }
                }
              });
              
              // 内容变化时自动处理标题ID
              ed.on('Change', () => {
                const content = ed.getContent();
                const tempDiv = document.createElement('div');
                tempDiv.innerHTML = content;
                const headings = tempDiv.querySelectorAll('h1, h2, h3, h4, h5, h6');
                
                let hasChanges = false;
                headings.forEach((heading) => {
                  if (!heading.id) {
                    const text = heading.textContent || heading.innerText || '';
                    const id = text.toLowerCase()
                      .replace(/[^\u4e00-\u9fa5a-zA-Z0-9\s]/g, '')
                      .replace(/\s+/g, '-')
                      .substring(0, 50);
                    if (id) {
                      heading.id = id;
                      hasChanges = true;
                    }
                  }
                });
                
                if (hasChanges) {
                  const newContent = tempDiv.innerHTML;
                  if (newContent !== content) {
                    ed.setContent(newContent);
                  }
                }
              });
              
              // 添加发布按钮
              ed.ui.registry.addButton('publish', {
                text: '发布',
                tooltip: '申请发布',
                onAction: () => {
                  if ((form.getFieldValue('pubStatus') ?? '') === 'in_review') {
                    message.warn('已申请，请等待').then(() => {});
                  } else if ((form.getFieldValue('pubStatus') ?? '') === 'inherit'){
                    message.info('已发布，请不要频繁修改').then();
                  }
                  else {
                    form.setFieldsValue({pubStatus: 'in_review'});
                    form.submit();
                    if (timerId.current)
                      clearTimeout(timerId.current);
                  }
                }
              });
            }
          }}
          onEditorChange={(content) => {
            setData(content);
            wait3s();
          }}
          value={data}
        />
      );
    }
  };

  return (
    <div className="editor-container" style={{ height: '100vh', display: 'flex', flexDirection: 'column' }}>
      {saveStatusAlert}
      <Form
        layout="vertical"
        style={{height: '41px'}}
        form={form}
        onFinish={doSave}
        hideRequiredMark
      >
        <FormItem name="parentId" noStyle>
          <Input type="hidden"/>
        </FormItem>
        <FormItem name="id" noStyle>
          <Input type="hidden"/>
        </FormItem>
        <FormItem name="pubStatus" noStyle>
          <Input type="hidden"/>
        </FormItem>
        <FormItem name="pubTitle"
          rules={[
            {
              required: true,
              max: 120,
            },
            {
              validator: checkTitle,
            }
          ]}
        >
          <Input
            size="large"
            {...inputProp}
            placeholder="请输入标题"
            onKeyUp={() => wait3s()}
            bordered={false}
          />
        </FormItem>
        <FormItem name="pubContent" noStyle>
          <textarea hidden
                    value={data} onChange={(e) => setData(e.target.value)}/>
        </FormItem>
      </Form>
      
      {/* 编辑器模式切换按钮 - 只在新建时显示 */}
      {!modeLocked && (
        <div style={{ 
          marginBottom: '16px', 
          display: 'flex', 
          gap: '8px',
          padding: '8px 0',
          borderBottom: '1px solid #f0f0f0'
        }}>
          <Button 
            type={editorMode === 'rich' ? 'primary' : 'default'}
            icon={<EditOutlined />}
            onClick={() => handleModeChange('rich')}
            size="small"
          >
            富文本
          </Button>
          <Button 
            type={editorMode === 'markdown' ? 'primary' : 'default'}
            icon={<EditOutlined />}
            onClick={() => handleModeChange('markdown')}
            size="small"
          >
            Markdown
          </Button>
        </div>
      )}
      {renderEditorContent()}
    </div>
  );
};



// Markdown相关函数
let isMarkdownMode = false;
let originalContent = '';

// HTML转Markdown的简单实现
function htmlToMarkdown(html) {
  let markdown = html;
  
  // 基本转换规则
  markdown = markdown.replace(/<h1[^>]*>(.*?)<\/h1>/gi, '# $1\n');
  markdown = markdown.replace(/<h2[^>]*>(.*?)<\/h2>/gi, '## $1\n');
  markdown = markdown.replace(/<h3[^>]*>(.*?)<\/h3>/gi, '### $1\n');
  markdown = markdown.replace(/<h4[^>]*>(.*?)<\/h4>/gi, '#### $1\n');
  markdown = markdown.replace(/<h5[^>]*>(.*?)<\/h5>/gi, '##### $1\n');
  markdown = markdown.replace(/<h6[^>]*>(.*?)<\/h6>/gi, '###### $1\n');
  
  markdown = markdown.replace(/<strong[^>]*>(.*?)<\/strong>/gi, '**$1**');
  markdown = markdown.replace(/<b[^>]*>(.*?)<\/b>/gi, '**$1**');
  markdown = markdown.replace(/<em[^>]*>(.*?)<\/em>/gi, '*$1*');
  markdown = markdown.replace(/<i[^>]*>(.*?)<\/i>/gi, '*$1*');
  
  markdown = markdown.replace(/<a[^>]*href="([^"]*)"[^>]*>(.*?)<\/a>/gi, '[$2]($1)');
  
  markdown = markdown.replace(/<ul[^>]*>(.*?)<\/ul>/gis, function(match, content) {
    return content.replace(/<li[^>]*>(.*?)<\/li>/gi, '- $1\n');
  });
  
  markdown = markdown.replace(/<ol[^>]*>(.*?)<\/ol>/gis, function(match, content) {
    let counter = 1;
    return content.replace(/<li[^>]*>(.*?)<\/li>/gi, function(match, content) {
      return `${counter++}. ${content}\n`;
    });
  });
  
  markdown = markdown.replace(/<code[^>]*>(.*?)<\/code>/gi, '`$1`');
  markdown = markdown.replace(/<pre[^>]*><code[^>]*>(.*?)<\/code><\/pre>/gis, '```\n$1\n```');
  
  markdown = markdown.replace(/<br[^>]*\/?>/gi, '\n');
  markdown = markdown.replace(/<p[^>]*>(.*?)<\/p>/gi, '$1\n\n');
  
  // 清理多余的标签
  markdown = markdown.replace(/<[^>]*>/g, '');
  
  // 清理多余的空行
  markdown = markdown.replace(/\n{3,}/g, '\n\n');
  
  return markdown.trim();
}

// 生成语法高亮的HTML
function generateSyntaxHighlightedHTML(code, language, isDarkMode) {
  try {
    const style = isDarkMode ? vscDarkPlus : vs;
    const highlightedHTML = renderToString(
      <SyntaxHighlighter
        style={style}
        language={language}
        PreTag="div"
        showLineNumbers={false}
        wrapLines={true}
        wrapLongLines={true}
        customStyle={{
          background: '#f6f8fa',
          margin: 0,
          padding: 0,
          border: 'none',
          borderRadius: 0
        }}
        codeTagProps={{
          style: {
            background: 'transparent'
          }
        }}
      >
        {code}
      </SyntaxHighlighter>
    );
    return highlightedHTML;
  } catch (error) {
    console.warn('语法高亮失败:', error);
    // 降级到普通代码块
    const escapedCode = code.replace(/</g, '&lt;').replace(/>/g, '&gt;');
    return `<pre style="background-color: #f6f8fa; border-radius: 6px; padding: 16px; overflow: auto; border: 1px solid #d0d7de; margin: 16px 0;"><code style="background: transparent; padding: 0; color: #24292e; font-family: 'JetBrains Mono', 'Fira Code', 'Cascadia Code', 'SF Mono', Monaco, 'Consolas', 'Liberation Mono', 'Courier New', monospace; display: block; white-space: pre;">${escapedCode}</code></pre>`;
  }
}

// Markdown转HTML的简单实现
function markdownToHtml(markdown, isDarkMode = false) {
  let html = markdown;
  
  // 标题
  html = html.replace(/^### (.*$)/gim, '<h3>$1</h3>');
  html = html.replace(/^## (.*$)/gim, '<h2>$1</h2>');
  html = html.replace(/^# (.*$)/gim, '<h1>$1</h1>');
  
  // 粗体和斜体
  html = html.replace(/\*\*(.*?)\*\*/g, '<strong>$1</strong>');
  html = html.replace(/\*(.*?)\*/g, '<em>$1</em>');
  
  // 链接
  html = html.replace(/\[([^\]]+)\]\(([^)]+)\)/g, '<a href="$2">$1</a>');
  
  // 图片处理 - 支持带HTML注释的markdown图片
  html = html.replace(/!\[([^\]]*)\]\(([^)]+)\)(?: "([^"]*)")?(?:\s*<!--\s*尺寸:\s*(\d+)x(\d+)\s*-->)?/g, (match, alt, src, title, width, height) => {
    let imgStyle = "max-width: 100%; height: auto; box-shadow: 0 2px 8px rgba(0,0,0,0.1);";
    
    // 如果指定了尺寸，应用具体尺寸
    if (width && height) {
      imgStyle = `width: ${width}px; height: ${height}px; box-shadow: 0 2px 8px rgba(0,0,0,0.1);`;
    }
    
    // 构建title属性
    let titleAttr = title ? ` title="${title}"` : '';
    
    return `<div style="text-align: center; margin: 16px 0;"><img src="${src}" alt="${alt || ''}"${titleAttr} style="${imgStyle}" onerror="this.style.display='none'; console.warn('图片加载失败:', '${src}');" /></div>`;
  });
  
  // 处理HTML格式的图片标签（保持原有样式和属性）
  html = html.replace(/<img([^>]*?)src="([^"]*)"([^>]*?)>/g, (match, beforeSrc, src, afterSrc) => {
    // 提取alt属性
    const altMatch = match.match(/alt="([^"]*)"/);
    const alt = altMatch ? altMatch[1] : '';
    
    // 检查是否已经有样式设置
    if (match.includes('style=') || match.includes('width=') || match.includes('height=')) {
      // 如果已经有样式，直接包装在div中
      return `<div style="text-align: center; margin: 16px 0;">${match}</div>`;
    } else {
      // 如果没有样式，添加默认样式
      return `<div style="text-align: center; margin: 16px 0;"><img src="${src}" alt="${alt}" style="max-width: 100%; height: auto; box-shadow: 0 2px 8px rgba(0,0,0,0.1);" onerror="this.style.display='none'; console.warn('图片加载失败:', '${src}');" /></div>`;
    }
  });
  
  // 代码块处理（使用真正的语法高亮）
  html = html.replace(/```(\w+)?\n([\s\S]*?)```/g, (match, lang, code) => {
    const language = lang || 'text';
    return generateSyntaxHighlightedHTML(code.trim(), language, isDarkMode);
  });
  
  // 行内代码
  html = html.replace(/`([^`]+)`/g, '<code style="background-color: rgba(27, 31, 35, 0.05); color: #2c3e50; padding: 0.2em 0.4em; border-radius: 3px; font-family: \'JetBrains Mono\', \'Fira Code\', \'Cascadia Code\', \'SF Mono\', Monaco, \'Consolas\', \'Liberation Mono\', \'Courier New\', monospace;">$1</code>');
  
  // 列表
  html = html.replace(/^\- (.*$)/gim, '<li>$1</li>');
  html = html.replace(/(<li>.*<\/li>)/s, '<ul>$1</ul>');
  
  // 段落
  html = html.replace(/\n\n/g, '</p><p>');
  html = '<p>' + html + '</p>';
  
  return html;
}

// 切换Markdown模式
function toggleMarkdownMode(editor) {
  if (isMarkdownMode) {
    // 从Markdown模式切换到富文本模式
    const markdownContent = editor.getContent();
    const htmlContent = markdownToHtml(markdownContent, isDarkTheme);
    editor.setContent(htmlContent);
    isMarkdownMode = false;
    
    // 更新模式指示器
    const modeButton = editor.theme.panel.find('toolbar').find('button[data-mce-name="mode_indicator"]')[0];
    if (modeButton) {
      modeButton.text('富文本');
      modeButton.aria('label', '当前模式：富文本');
    }
    
    message.success('已切换到富文本模式');
  } else {
    // 从富文本模式切换到Markdown模式
    originalContent = editor.getContent();
    const markdownContent = htmlToMarkdown(originalContent);
    editor.setContent(markdownContent);
    isMarkdownMode = true;
    
    // 更新模式指示器
    const modeButton = editor.theme.panel.find('toolbar').find('button[data-mce-name="mode_indicator"]')[0];
    if (modeButton) {
      modeButton.text('Markdown');
      modeButton.aria('label', '当前模式：Markdown');
    }
    
    message.success('已切换到Markdown模式');
  }
}

// 显示Markdown预览
function showMarkdownPreview(editor) {
  const content = editor.getContent();
  let htmlContent;
  
  if (isMarkdownMode) {
    // 如果是Markdown模式，直接转换
    htmlContent = markdownToHtml(content, isDarkTheme);
  } else {
    // 如果是富文本模式，先转换为Markdown再转换回HTML
    const markdown = htmlToMarkdown(content);
    htmlContent = markdownToHtml(markdown, isDarkTheme);
  }
  
  editor.windowManager.open({
    title: 'Markdown预览',
    body: {
      type: 'panel',
      items: [{
        type: 'htmlpanel',
        html: `
          <div style="padding: 20px; max-height: 500px; overflow-y: auto; font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif;">
            <style>
              h1, h2, h3, h4, h5, h6 { margin-top: 24px; margin-bottom: 16px; font-weight: 600; line-height: 1.25; }
              h1 { font-size: 2em; border-bottom: 1px solid #eaecef; padding-bottom: 0.3em; }
              h2 { font-size: 1.5em; border-bottom: 1px solid #eaecef; padding-bottom: 0.3em; }
              h3 { font-size: 1.25em; }
              p { margin-bottom: 16px; line-height: 1.6; }
              ul, ol { margin-bottom: 16px; padding-left: 2em; }
              li { margin-bottom: 4px; }
              strong { font-weight: 600; }
              em { font-style: italic; }
              code { padding: 0.2em 0.4em; background-color: rgba(27, 31, 35, 0.05); border-radius: 3px; font-family: 'Monaco', 'Menlo', 'Ubuntu Mono', monospace; }
              pre { background-color: #f6f8fa; border-radius: 6px; padding: 16px; overflow: auto; }
              pre code { background: transparent; padding: 0; }
              a { color: #0366d6; text-decoration: none; }
              a:hover { text-decoration: underline; }
            </style>
            ${htmlContent}
          </div>
        `
      }]
    },
    buttons: [{
      type: 'cancel',
      text: '关闭'
    }],
    size: 'large'
  });
}

// Markdown图片插入/编辑对话框
function showMarkdownImageDialog(existingImage = null, formInstance = null, editorInstance = null) {
  // 暴露到全局，供脚本调用
  window.showMarkdownImageDialog = showMarkdownImageDialog;
  // 创建遮罩层
  const backdrop = document.createElement('div');
  backdrop.className = 'markdown-image-dialog-backdrop';
  backdrop.style.cssText = `
    position: fixed;
    top: 0;
    left: 0;
    width: 100%;
    height: 100%;
    background-color: rgba(0, 0, 0, 0.5);
    z-index: 999999;
  `;
  
  // 创建对话框
  const dialog = document.createElement('div');
  dialog.className = 'markdown-image-dialog';
  dialog.style.cssText = `
    position: fixed;
    top: 50%;
    left: 50%;
    transform: translate(-50%, -50%);
    background: white;
    border-radius: 8px;
    box-shadow: 0 4px 20px rgba(0, 0, 0, 0.15);
    width: 500px;
    max-width: 90vw;
    z-index: 999999;
    padding: 0;
  `;
  
  // 创建对话框内容
  dialog.innerHTML = `
    <div style="padding: 20px 20px 0 20px;">
      <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 20px;">
        <h3 style="margin: 0; color: #333;">插入/编辑图片</h3>
        <button id="closeBtn" style="background: none; border: none; font-size: 20px; cursor: pointer; color: #999;">&times;</button>
      </div>
      
      <!-- 标签页 -->
      <div style="display: flex; border-bottom: 1px solid #e8e8e8; margin-bottom: 20px;">
        <button id="urlTab" class="tab-btn active" style="padding: 8px 16px; border: none; background: none; border-bottom: 2px solid #1890ff; color: #1890ff; cursor: pointer;">普通</button>
        <button id="uploadTab" class="tab-btn" style="padding: 8px 16px; border: none; background: none; border-bottom: 2px solid transparent; color: #666; cursor: pointer;">上传</button>
      </div>
      
      <!-- URL标签页内容 -->
      <div id="urlTabContent" class="tab-content">
        <div style="margin-bottom: 15px;">
          <label style="display: block; margin-bottom: 5px; font-weight: 500;">地址:</label>
          <input type="url" id="imageUrl" placeholder="请输入图片URL" style="width: 100%; padding: 8px; border: 1px solid #d9d9d9; border-radius: 4px; box-sizing: border-box;">
        </div>
        <div style="margin-bottom: 15px;">
          <label style="display: block; margin-bottom: 5px; font-weight: 500;">图片名称:</label>
          <input type="text" id="imageName" placeholder="图片名称" style="width: 100%; padding: 8px; border: 1px solid #d9d9d9; border-radius: 4px; box-sizing: border-box;">
        </div>
        <div style="margin-bottom: 15px;">
          <label style="display: block; margin-bottom: 5px; font-weight: 500;">Alternative description:</label>
          <input type="text" id="imageAlt" placeholder="图片描述" style="width: 100%; padding: 8px; border: 1px solid #d9d9d9; border-radius: 4px; box-sizing: border-box;">
        </div>
        <div style="display: flex; gap: 10px; margin-bottom: 15px;">
          <div style="flex: 1;">
            <label style="display: block; margin-bottom: 5px; font-weight: 500;">宽:</label>
            <input type="number" id="imageWidth" placeholder="例如: 300" style="width: 100%; padding: 8px; border: 1px solid #d9d9d9; border-radius: 4px; box-sizing: border-box;">
          </div>
          <div style="flex: 1; position: relative;">
            <label style="display: block; margin-bottom: 5px; font-weight: 500;">高:</label>
            <input type="number" id="imageHeight" placeholder="例如: 200" style="width: 100%; padding: 8px; border: 1px solid #d9d9d9; border-radius: 4px; box-sizing: border-box;">
            <button id="lockRatioBtn" style="position: absolute; right: 8px; top: 28px; background: none; border: none; cursor: pointer; font-size: 16px;" title="锁定比例">🔒</button>
          </div>
        </div>
      </div>
      
      <!-- 上传标签页内容 -->
      <div id="uploadTabContent" class="tab-content" style="display: none;">
        <div style="margin-bottom: 15px;">
          <label style="display: block; margin-bottom: 5px; font-weight: 500;">选择图片:</label>
          <input type="file" id="imageFile" accept="image/*" style="width: 100%; padding: 8px; border: 1px solid #d9d9d9; border-radius: 4px; box-sizing: border-box;">
        </div>
        <div style="margin-bottom: 15px;">
          <label style="display: block; margin-bottom: 5px; font-weight: 500;">图片名称:</label>
          <input type="text" id="uploadImageName" placeholder="图片名称" style="width: 100%; padding: 8px; border: 1px solid #d9d9d9; border-radius: 4px; box-sizing: border-box;">
        </div>
        <div style="margin-bottom: 15px;">
          <label style="display: block; margin-bottom: 5px; font-weight: 500;">Alternative description:</label>
          <input type="text" id="uploadImageAlt" placeholder="图片描述" style="width: 100%; padding: 8px; border: 1px solid #d9d9d9; border-radius: 4px; box-sizing: border-box;">
        </div>
        <div style="display: flex; gap: 10px; margin-bottom: 15px;">
          <div style="flex: 1;">
            <label style="display: block; margin-bottom: 5px; font-weight: 500;">宽:</label>
            <input type="number" id="uploadImageWidth" placeholder="例如: 300" style="width: 100%; padding: 8px; border: 1px solid #d9d9d9; border-radius: 4px; box-sizing: border-box;">
          </div>
          <div style="flex: 1; position: relative;">
            <label style="display: block; margin-bottom: 5px; font-weight: 500;">高:</label>
            <input type="number" id="uploadImageHeight" placeholder="例如: 200" style="width: 100%; padding: 8px; border: 1px solid #d9d9d9; border-radius: 4px; box-sizing: border-box;">
            <button id="uploadLockRatioBtn" style="position: absolute; right: 8px; top: 28px; background: none; border: none; cursor: pointer; font-size: 16px;" title="锁定比例">🔒</button>
          </div>
        </div>
      </div>
    </div>
    
    <div style="padding: 20px; border-top: 1px solid #e8e8e8; display: flex; justify-content: flex-end; gap: 10px;">
      <button id="cancelBtn" style="padding: 8px 16px; border: 1px solid #d9d9d9; background: white; border-radius: 4px; cursor: pointer;">取消</button>
      <button id="saveBtn" style="padding: 8px 16px; border: none; background: #1890ff; color: white; border-radius: 4px; cursor: pointer;">保存</button>
    </div>
  `;
  
  // 添加到页面
  document.body.appendChild(backdrop);
  document.body.appendChild(dialog);
  
  // 如果是编辑已有图片，填充数据
  if (existingImage) {
    const urlInput = dialog.querySelector('#imageUrl');
    const nameInput = dialog.querySelector('#imageName');
    const altInput = dialog.querySelector('#imageAlt');
    const widthInput = dialog.querySelector('#imageWidth');
    const heightInput = dialog.querySelector('#imageHeight');
    
    urlInput.value = existingImage.url || '';
    nameInput.value = existingImage.name || '';
    altInput.value = existingImage.alt || '';
    widthInput.value = existingImage.width || '';
    heightInput.value = existingImage.height || '';
  }
  
  // 标签页切换功能
  const urlTab = dialog.querySelector('#urlTab');
  const uploadTab = dialog.querySelector('#uploadTab');
  const urlTabContent = dialog.querySelector('#urlTabContent');
  const uploadTabContent = dialog.querySelector('#uploadTabContent');
  
  urlTab.onclick = () => {
    urlTab.classList.add('active');
    uploadTab.classList.remove('active');
    urlTabContent.style.display = 'block';
    uploadTabContent.style.display = 'none';
    urlTab.style.borderBottomColor = '#1890ff';
    urlTab.style.color = '#1890ff';
    uploadTab.style.borderBottomColor = 'transparent';
    uploadTab.style.color = '#666';
  };
  
  uploadTab.onclick = () => {
    uploadTab.classList.add('active');
    urlTab.classList.remove('active');
    uploadTabContent.style.display = 'block';
    urlTabContent.style.display = 'none';
    uploadTab.style.borderBottomColor = '#1890ff';
    uploadTab.style.color = '#1890ff';
    urlTab.style.borderBottomColor = 'transparent';
    urlTab.style.color = '#666';
  };
  
  // 比例锁定功能
  let isLocked = true;
  let aspectRatio = 1;
  
  const setupRatioLock = (widthInput, heightInput, lockBtn) => {
    const updateHeight = () => {
      if (isLocked && widthInput.value && aspectRatio) {
        heightInput.value = Math.round(widthInput.value / aspectRatio);
      }
    };
    
    const updateWidth = () => {
      if (isLocked && heightInput.value && aspectRatio) {
        widthInput.value = Math.round(heightInput.value * aspectRatio);
      }
    };
    
    widthInput.addEventListener('input', updateHeight);
    heightInput.addEventListener('input', updateWidth);
    
    lockBtn.onclick = () => {
      isLocked = !isLocked;
      lockBtn.textContent = isLocked ? '🔒' : '🔓';
      lockBtn.title = isLocked ? '锁定比例' : '解锁比例';
    };
  };
  
  // 为URL标签页设置比例锁定
  const urlInput = dialog.querySelector('#imageUrl');
  const urlWidthInput = dialog.querySelector('#imageWidth');
  const urlHeightInput = dialog.querySelector('#imageHeight');
  const urlLockBtn = dialog.querySelector('#lockRatioBtn');
  setupRatioLock(urlWidthInput, urlHeightInput, urlLockBtn);
  
  // 为上传标签页设置比例锁定
  const uploadWidthInput = dialog.querySelector('#uploadImageWidth');
  const uploadHeightInput = dialog.querySelector('#uploadImageHeight');
  const uploadLockBtn = dialog.querySelector('#uploadLockRatioBtn');
  setupRatioLock(uploadWidthInput, uploadHeightInput, uploadLockBtn);
  
  // 图片URL变化时获取尺寸
  urlInput.addEventListener('input', () => {
    if (urlInput.value) {
      const img = new Image();
      img.onload = function() {
        aspectRatio = this.naturalWidth / this.naturalHeight;
        if (!urlWidthInput.value) urlWidthInput.value = this.naturalWidth;
        if (!urlHeightInput.value) urlHeightInput.value = this.naturalHeight;
      };
      img.src = urlInput.value;
    }
  });
  
  // 文件选择时获取尺寸
  const fileInput = dialog.querySelector('#imageFile');
  fileInput.addEventListener('change', (e) => {
    const file = e.target.files[0];
    if (file) {
      const reader = new FileReader();
      reader.onload = (e) => {
        const img = new Image();
        img.onload = function() {
          aspectRatio = this.naturalWidth / this.naturalHeight;
          uploadWidthInput.value = this.naturalWidth;
          uploadHeightInput.value = this.naturalHeight;
        };
        img.src = e.target.result;
      };
      reader.readAsDataURL(file);
    }
  });
  
  // 绑定事件
  const closeBtn = dialog.querySelector('#closeBtn');
  const cancelBtn = dialog.querySelector('#cancelBtn');
  const saveBtn = dialog.querySelector('#saveBtn');
  
  const closeDialog = () => {
    document.body.removeChild(backdrop);
    document.body.removeChild(dialog);
  };
  
  closeBtn.onclick = closeDialog;
  cancelBtn.onclick = closeDialog;
  backdrop.onclick = closeDialog;
  
  saveBtn.onclick = async () => {
    const isUploadTab = uploadTab.classList.contains('active');
    
    if (isUploadTab) {
      // 上传模式
      const file = fileInput.files[0];
      if (!file) {
        message.warn('请选择图片文件');
        return;
      }
      
      // 检查文件大小
      const isGt15M = file.size / 1024 / 1024 > 15;
      if (isGt15M) {
        message.warn('图片尺寸超限, 请压缩后上传');
        return;
      }
      
      try {
        message.loading('正在上传图片...', 0);
        
        const formData = new FormData();
        formData.append("file", file);
        if (formInstance) {
          formData.append("id", formInstance.getFieldValue("id"));
        }
        const res = await uploadFile(formData);
        
        message.destroy();
        
        if (res?.data?.url) {
          const uploadImageAlt = dialog.querySelector('#uploadImageAlt');
          const uploadImageName = dialog.querySelector('#uploadImageName');
          insertImage(res.data.url, uploadImageAlt.value || file.name, uploadImageWidth.value, uploadImageHeight.value, uploadImageName.value);
          closeDialog();
          message.success('图片上传并插入成功');
        } else {
          message.error('图片上传失败');
        }
      } catch (error) {
        message.destroy();
        message.error('图片上传失败: ' + error.message);
      }
    } else {
      // URL模式
      const url = urlInput.value.trim();
      if (!url) {
        message.warn('请输入图片URL');
        return;
      }
      
      const imageAlt = dialog.querySelector('#imageAlt');
      const imageName = dialog.querySelector('#imageName');
      insertImage(url, imageAlt.value || '图片', urlWidthInput.value, urlHeightInput.value, imageName.value);
      closeDialog();
      message.success('图片插入成功');
    }
  };
  
  // 插入图片到编辑器
  function insertImage(url, alt, width, height, imageName = '') {
    let imageMarkdown;
    
    // 始终使用标准markdown语法，即使设置了宽高
    // 标准markdown语法：![alt](url "title")
    if (imageName) {
      imageMarkdown = `![${alt}](${url} "${imageName}")`;
    } else {
      imageMarkdown = `![${alt}](${url})`;
    }
    
    // 如果设置了宽高，在markdown后面添加HTML注释说明尺寸
    if (width || height) {
      imageMarkdown += ` <!-- 尺寸: ${width || 'auto'}x${height || 'auto'} -->`;
    }
    
    // 插入到编辑器中
    if (editorInstance) {
      const doc = editorInstance.state.doc;
      const selection = editorInstance.state.selection;
      const insertPos = selection.main.head;
      const newText = `\n${imageMarkdown}\n`;
      editorInstance.dispatch({
        changes: {
          from: insertPos,
          to: insertPos,
          insert: newText
        }
      });
    } else {
      const textarea = document.querySelector('#codemirror-editor textarea');
      if (textarea) {
        const start = textarea.selectionStart;
        const end = textarea.selectionEnd;
        const before = markdownContent.substring(0, start);
        const after = markdownContent.substring(end);
        const newContent = before + `\n${imageMarkdown}\n` + after;
        handleMarkdownChange(newContent);
        
        setTimeout(() => {
          textarea.focus();
          textarea.setSelectionRange(start + imageMarkdown.length + 2, start + imageMarkdown.length + 2);
        }, 0);
      } else {
        const newContent = markdownContent + `\n\n${imageMarkdown}\n`;
        handleMarkdownChange(newContent);
      }
    }
  }
}

// 图片编辑对话框
function showImageEditDialog(imageUrl, imageAlt, format = 'html') {
  // 创建遮罩层
  const backdrop = document.createElement('div');
  backdrop.className = 'image-edit-backdrop';
  backdrop.style.cssText = `
    position: fixed;
    top: 0;
    left: 0;
    width: 100%;
    height: 100%;
    background-color: rgba(0, 0, 0, 0.5);
    z-index: 999999;
  `;
  
  // 创建对话框
  const dialog = document.createElement('div');
  dialog.className = 'image-edit-dialog';
  dialog.style.cssText = `
    position: fixed;
    top: 50%;
    left: 50%;
    transform: translate(-50%, -50%);
    background: white;
    border-radius: 8px;
    box-shadow: 0 4px 20px rgba(0, 0, 0, 0.15);
    width: 450px;
    max-width: 90vw;
    z-index: 999999;
    padding: 20px;
  `;
  
  // 创建表单内容
  dialog.innerHTML = `
    <div style="margin-bottom: 20px;">
      <h3 style="margin: 0 0 15px 0; color: #333;">编辑图片</h3>
      <div style="margin-bottom: 15px;">
        <label style="display: block; margin-bottom: 5px; font-weight: 500;">图片描述:</label>
        <input type="text" id="editImageAlt" value="${imageAlt}" style="width: 100%; padding: 8px; border: 1px solid #d9d9d9; border-radius: 4px; box-sizing: border-box;">
      </div>
      <div style="margin-bottom: 15px;">
        <div style="display: flex; align-items: center; margin-bottom: 5px;">
          <label style="font-weight: 500; margin-right: 10px;">显示宽度:</label>
          <label style="display: flex; align-items: center; cursor: pointer; font-size: 12px; color: #666;">
            <input type="checkbox" id="editLockRatio" checked style="margin-right: 4px;">
            <span>锁定比例</span>
          </label>
        </div>
        <input type="number" id="editImageWidth" placeholder="例如: 300" style="width: 100%; padding: 8px; border: 1px solid #d9d9d9; border-radius: 4px; box-sizing: border-box;">
      </div>
      <div style="margin-bottom: 15px;">
        <label style="display: block; margin-bottom: 5px; font-weight: 500;">显示高度:</label>
        <input type="number" id="editImageHeight" placeholder="例如: 200" style="width: 100%; padding: 8px; border: 1px solid #d9d9d9; border-radius: 4px; box-sizing: border-box;">
      </div>
      <div style="margin-bottom: 20px;">
        <label style="display: flex; align-items: center; cursor: pointer;">
          <input type="checkbox" id="editImageCenter" style="margin-right: 8px;">
          <span>居中显示</span>
        </label>
      </div>
    </div>
    <div style="display: flex; justify-content: flex-end; gap: 10px;">
      <button id="editCancelBtn" style="padding: 8px 16px; border: 1px solid #d9d9d9; background: white; border-radius: 4px; cursor: pointer;">取消</button>
      <button id="editConfirmBtn" style="padding: 8px 16px; border: none; background: #1890ff; color: white; border-radius: 4px; cursor: pointer;">确认修改</button>
    </div>
  `;
  
  // 添加到页面
  document.body.appendChild(backdrop);
  document.body.appendChild(dialog);
  
  // 获取图片原始尺寸
  const img = new Image();
  img.onload = function() {
    const originalWidth = this.naturalWidth;
    const originalHeight = this.naturalHeight;
    const aspectRatio = originalWidth / originalHeight;
    
    // 设置默认值（原始尺寸）
    const widthInput = dialog.querySelector('#editImageWidth');
    const heightInput = dialog.querySelector('#editImageHeight');
    const lockRatioCheckbox = dialog.querySelector('#editLockRatio');
    
    widthInput.value = originalWidth;
    heightInput.value = originalHeight;
    
    // 比例锁定功能
    let isLocked = true;
    
    const updateHeight = () => {
      if (isLocked && widthInput.value) {
        heightInput.value = Math.round(widthInput.value / aspectRatio);
      }
    };
    
    const updateWidth = () => {
      if (isLocked && heightInput.value) {
        widthInput.value = Math.round(heightInput.value * aspectRatio);
      }
    };
    
    widthInput.addEventListener('input', updateHeight);
    heightInput.addEventListener('input', updateWidth);
    
    lockRatioCheckbox.addEventListener('change', (e) => {
      isLocked = e.target.checked;
    });
  };
  img.src = imageUrl;
  
  // 绑定事件
  const cancelBtn = dialog.querySelector('#editCancelBtn');
  const confirmBtn = dialog.querySelector('#editConfirmBtn');
  
  const closeDialog = () => {
    document.body.removeChild(backdrop);
    document.body.removeChild(dialog);
  };
  
  cancelBtn.onclick = closeDialog;
  backdrop.onclick = closeDialog;
  
  confirmBtn.onclick = () => {
    const alt = dialog.querySelector('#editImageAlt').value || '图片';
    const width = dialog.querySelector('#editImageWidth').value;
    const height = dialog.querySelector('#editImageHeight').value;
    const center = dialog.querySelector('#editImageCenter').checked;
    
    // 生成新的图片引用
    let newImageMarkdown;
    
    if (width || height || center) {
      // 使用HTML格式
      let imgAttributes = '';
      if (width) imgAttributes += ` width="${width}"`;
      if (height) imgAttributes += ` height="${height}"`;
      if (center) imgAttributes += ' style="display: block; margin: 0 auto;"';
      
      newImageMarkdown = `<img src="${imageUrl}" alt="${alt}"${imgAttributes}>`;
    } else {
      // 使用标准markdown语法
      newImageMarkdown = `![${alt}](${imageUrl})`;
    }
    
    // 替换编辑器中的图片
    const markdownContent = document.querySelector('#codemirror-editor textarea');
    if (markdownContent) {
      const text = markdownContent.value;
      let newText;
      
      if (format === 'html') {
        // 替换HTML格式的图片
        newText = text.replace(/<img[^>]*src="[^"]*"[^>]*>/g, (match) => {
          if (match.includes(imageUrl)) {
            return newImageMarkdown;
          }
          return match;
        });
      } else {
        // 替换markdown格式的图片
        newText = text.replace(/!\[([^\]]*)\]\(([^)]+)\)/g, (match, alt, src) => {
          if (src === imageUrl) {
            return newImageMarkdown;
          }
          return match;
        });
      }
      
      handleMarkdownChange(newText);
    }
    
    closeDialog();
    message.success('图片修改成功');
  };
}

export default BookView;
