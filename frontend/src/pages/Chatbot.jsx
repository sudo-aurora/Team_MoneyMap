import { useState } from 'react';
import {
  Box,
  Paper,
  TextField,
  Button,
  Typography,
  Avatar,
  IconButton,
  Container,
} from '@mui/material';
import {
  Send as SendIcon,
  SmartToy as BotIcon,
  Person as PersonIcon,
} from '@mui/icons-material';
import axios from 'axios';
import ReactMarkdown from 'react-markdown';

export default function Chatbot() {
  const [messages, setMessages] = useState([]);
  const [inputMessage, setInputMessage] = useState('');
  const [isLoading, setIsLoading] = useState(false);

  const sendMessage = async () => {
    if (!inputMessage.trim()) return;

    const userMessage = {
      text: inputMessage,
      sender: 'user',
      timestamp: new Date(),
    };

    setMessages(prev => [...prev, userMessage]);
    setInputMessage('');
    setIsLoading(true);

    try {
      const response = await axios.post('/api/v1/ai/chat', {
        message: inputMessage,
      });

      const botMessage = {
        text: response.data.response,
        sender: 'bot',
        timestamp: new Date(),
      };

      setMessages(prev => [...prev, botMessage]);
    } catch (error) {
      console.error('Error sending message:', error);
      const errorMessage = {
        text: 'Sorry, I encountered an error. Please try again later.',
        sender: 'bot',
        timestamp: new Date(),
      };
      setMessages(prev => [...prev, errorMessage]);
    } finally {
      setIsLoading(false);
    }
  };

  const handleKeyPress = (e) => {
    if (e.key === 'Enter' && !e.shiftKey) {
      e.preventDefault();
      sendMessage();
    }
  };

  return (
    <Container maxWidth="md" sx={{ height: '100%', display: 'flex', flexDirection: 'column' }}>
      <Typography variant="h4" gutterBottom fontWeight="bold" color="primary">
        💬 MoneyMap AI Assistant
      </Typography>
      
      <Paper
        sx={{
          flex: 1,
          display: 'flex',
          flexDirection: 'column',
          overflow: 'hidden',
          mb: 2,
        }}
      >
        <Box
          sx={{
            flex: 1,
            overflow: 'auto',
            p: 2,
            display: 'flex',
            flexDirection: 'column',
            gap: 2,
          }}
        >
          {messages.length === 0 && (
            <Box sx={{ textAlign: 'center', py: 4 }}>
              <Avatar sx={{ mx: 'auto', mb: 2, bgcolor: 'primary.main' }}>
                <BotIcon />
              </Avatar>
              <Typography variant="h6" color="text.secondary">
                👋 Welcome to MoneyMap AI Assistant!
              </Typography>
              <Typography variant="body2" color="text.secondary">
                Ask me anything about your portfolio, clients, or financial data.
              </Typography>
            </Box>
          )}

          {messages.map((message, index) => (
            <Box
              key={index}
              sx={{
                display: 'flex',
                justifyContent: message.sender === 'user' ? 'flex-end' : 'flex-start',
                gap: 1,
              }}
            >
              {message.sender === 'bot' && (
                <Avatar sx={{ bgcolor: 'primary.main' }}>
                  <BotIcon />
                </Avatar>
              )}
              
              <Paper
                sx={{
                  p: 2,
                  maxWidth: '70%',
                  bgcolor: message.sender === 'user' ? 'primary.main' : 'grey.100',
                  color: message.sender === 'user' ? 'white' : 'text.primary',
                }}
              >
                {message.sender === 'bot' ? (
                  <Box
                    sx={{
                      '& h1, h2, h3, h4, h5, h6': {
                        color: 'inherit',
                        mt: 1,
                        mb: 1,
                      },
                      '& strong': {
                        fontWeight: 'bold',
                        color: 'primary.main',
                      },
                      '& ul, ol': {
                        pl: 2,
                        my: 1,
                      },
                      '& li': {
                        my: 0.5,
                      },
                      '& p': {
                        my: 1,
                      },
                    }}
                    dangerouslySetInnerHTML={{ 
                      __html: message.text
                        .replace(/\*\*(.*?)\*\*/g, '<strong>$1</strong>') // Bold text
                        .replace(/\*(.*?)\*/g, '<em>$1</em>') // Italic text
                        .replace(/^### (.*$)/gim, '<h3>$1</h3>') // H3 headers
                        .replace(/^## (.*$)/gim, '<h2>$1</h2>') // H2 headers
                        .replace(/^# (.*$)/gim, '<h1>$1</h1>') // H1 headers
                        .replace(/^\* (.*)$/gim, '<li>$1</li>') // Bullet points
                        .replace(/(<li>.*<\/li>)/s, '<ul>$1</ul>') // Wrap list items
                        .replace(/^\d+\. (.*)$/gim, '<li>$1</li>') // Numbered lists
                        .replace(/\n\n/g, '</p><p>') // Paragraphs
                        .replace(/\n/g, '<br>') // Line breaks
                        .replace(/^(.*)$/, '<p>$1</p>') // Wrap in paragraphs
                    }}
                  />
                ) : (
                  <Typography variant="body1" sx={{ whiteSpace: 'pre-wrap' }}>
                    {message.text}
                  </Typography>
                )}
              </Paper>

              {message.sender === 'user' && (
                <Avatar sx={{ bgcolor: 'secondary.main' }}>
                  <PersonIcon />
                </Avatar>
              )}
            </Box>
          ))}

          {isLoading && (
            <Box sx={{ display: 'flex', gap: 1, alignItems: 'center' }}>
              <Avatar sx={{ bgcolor: 'primary.main' }}>
                <BotIcon />
              </Avatar>
              <Paper sx={{ p: 2, bgcolor: 'grey.100' }}>
                <Typography variant="body2" color="text.secondary">
                  🤔 Thinking...
                </Typography>
              </Paper>
            </Box>
          )}
        </Box>

        <Box sx={{ p: 2, borderTop: 1, borderColor: 'divider' }}>
          <Box sx={{ display: 'flex', gap: 1 }}>
            <TextField
              fullWidth
              variant="outlined"
              placeholder="Ask me about your portfolio..."
              value={inputMessage}
              onChange={(e) => setInputMessage(e.target.value)}
              onKeyPress={handleKeyPress}
              disabled={isLoading}
              multiline
              maxRows={3}
            />
            <IconButton
              color="primary"
              onClick={sendMessage}
              disabled={!inputMessage.trim() || isLoading}
              sx={{ alignSelf: 'flex-end' }}
            >
              <SendIcon />
            </IconButton>
          </Box>
        </Box>
      </Paper>
    </Container>
  );
}
